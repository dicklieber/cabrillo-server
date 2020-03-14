
package controllers

import java.time.{LocalDate, LocalTime}

import akka.actor.ActorRef
import be.objectify.deadbolt.scala.ActionBuilders
import com.typesafe.config.Config
import com.wa9nnn.wfdserver.auth.{SubjectAccess, WfdSubject}
import com.wa9nnn.wfdserver.htmlTable.Table
import com.wa9nnn.wfdserver.model.{CallSign, PaperLogQso}
import com.wa9nnn.wfdserver.paper._
import com.wa9nnn.wfdserver.util.{JsonLogging, Page}
import javax.inject.{Inject, Named, Singleton}
import play.api.data.Form
import play.api.mvc._
import akka.pattern._
import akka.util.Timeout
import scala.language.postfixOps
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.language.implicitConversions

@Singleton
class PaperLogController @Inject()(cc: ControllerComponents, actionBuilder: ActionBuilders,
                                   @Named("paperSessions") sessions: ActorRef,
                                   paperLogsDao: PaperLogsDao,
                                   forms: PaperForms)
                                  (implicit exec: ExecutionContext, config: Config,
                                   addManyOp: AddMany, sectionChoices: SectionChoices)
  extends AbstractController(cc)
    with JsonLogging with play.api.i18n.I18nSupport
    with SubjectAccess {

  private implicit val dates: Seq[LocalDate] = {
    val date0 = LocalDate.parse(config.getString("wfd.startDate"))
    Seq(date0, date0.plusDays(1))
  }

  implicit val timeout: Timeout = Timeout(3 seconds)

  //  private val byCallSign = new TrieMap[CallSign, PaperLogDao]()

  //  private def paperLog(callSign: CallSign)(implicit request: Request[AnyContent]): SessionDao = {
  //    byCallSign.getOrElseUpdate(callSign, paperLogsDao.start(callSign))
  //  }

  //  def index(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
  //    implicit request =>
  //      (sessions ? LogList).mapTo[Table].map { table =>
  //        Ok(views.html.paperLogList(paperLogsDao.))
  //      }
  //  }

  def paperLogList: Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>

      Future(Ok(views.html.paperLogList(paperLogsDao.table)))
  }

  def create(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      val callSign = request.body.asFormUrlEncoded.get.head._2.head

      (sessions ? StartSession(callSign, subject)).mapTo[Table].map { table =>
        Ok(views.html.paperLogList(table))
      }

    //
    //
    //      val pl: PaperLog = sessions.session(callSign).paperLog
    //
    //      val paperHeaderForm: Form[PaperLogHeader] = forms.header.fill(pl.paperLogHeader)
    //      //      val paperQsoForm: Form[PaperLogQso] = formQso.fill(PaperLogQso(callSign = callSign))
    //      Future(Ok(views.html.paperLogHeader(Some(callSign), paperHeaderForm)))
  }

  /**
   * from click on callSign in list.
   *
   * @param callSign of interest.
   */
  def start(callSign: CallSign): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>

      session(callSign) { dao =>
        val pl: PaperLog = dao.paperLog
        val header = pl.paperLogHeader
        if (header.isvalid) {
          val plf: Form[PaperLogHeader] = forms.header.fill(header)
          Ok(views.html.paperLogHeader(Some(callSign), plf))
        } else {
          doQsoEdit(dao)
        }
      }
  }

  implicit def csToOpt(callSign: CallSign): Option[CallSign] = Option(callSign)


  private def doQsoEdit(sessionDao: SessionDao, qso: Option[PaperLogQso] = None, editIndex: Option[Int] = None)(implicit request: Request[AnyContent]): Result = {

    val qsosTable = sessionDao.qsosTable(Page.last, editIndex = editIndex)
    val callSign = sessionDao.callSign
    val paperLogQso = qso.getOrElse(PaperLogQso(callSign))
    val paperQsoForm: Form[PaperLogQso] = forms.qso.fill(paperLogQso)
    Ok(views.html.qsoEditor(callSign, paperQsoForm, qsosTable))
  }

  def qsoEditor(callSign: Option[CallSign]): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      callSign match {
        case Some(cs) =>
          session(cs) {
            doQsoEdit(_)
          }
        case None =>
          Future(Ok("Please select or create a callSign in the logs tab."))
      }
  }


  def headerEditor(callSign: Option[CallSign]): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      callSign match {
        case Some(cs) =>
          (sessions ? SessionRequest(cs, subject)).mapTo[SessionDao].map { sessionDao =>
            val plHeader: PaperLogHeader = sessionDao.header
            val form: Form[PaperLogHeader] = forms.header.fill(plHeader)
            Ok(views.html.paperLogHeader(callSign, form))
          }
        case None =>
          Future(Ok("Please select or create a callSign in the logs tab."))
      }
  }

  /**
   *
   * @param callSign of cabrillo
   * @param f        something that returns a [[Result]]
   * @param subject  who
   * @return the [[Result]] in a [[Future]].
   */
  def session(callSign: CallSign)(f: (SessionDao) => Result)(implicit subject: WfdSubject): Future[Result] = {
    (sessions ? SessionRequest(callSign, subject)).mapTo[SessionDao].map(f(_))
  }

  def updateHeader(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      forms.header.bindFromRequest.fold(
        formWithErrors => {
          val maybeString = Option(formWithErrors.data.get("callSign")
            .map(CallSign(_))
            .getOrElse(CallSign.empty))
          Future(BadRequest(views.html.paperLogHeader(maybeString, formWithErrors)))
        },
        paperLogHeader => {
          val callSign = paperLogHeader.callSign

          session(callSign) { sessionDao =>
            sessionDao.saveHeader(paperLogHeader)
            Redirect(routes.PaperLogController.headerEditor(Option(paperLogHeader.callSign)))
          }
        }
      )
  }

  def addQso(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      forms.qso.bindFromRequest.fold(
        (formWithErrors: Form[PaperLogQso]) => {
          val callSign: String = formWithErrors.data("callSign")
          session(callSign) { sessionDao =>
            BadRequest(views.html.qsoEditor(CallSign(callSign), formWithErrors, sessionDao.qsosTable(Page.last)))
          }
        },
        paperLogQso => {
          session(paperLogQso.callSign) { sessionDao =>
            sessionDao.addQso(paperLogQso)
            doQsoEdit(sessionDao, None, None)
          }
        }
      )
  }

  def deleteQso(index: Int, callSign: CallSign): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      session(callSign) { sessionDao =>
        sessionDao.remove(index)
        doQsoEdit(sessionDao)
      }
  }

  /**
   * Edit existing QSO at index
   *
   * @param callSign of cabrillo.
   * @param index    in cache. From [[PaperLogQso.index]].
   */
  def editQso(callSign: CallSign, index: Int): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      session(callSign) { sessionDao =>
        sessionDao.get(index) match {
          case Some(qso) =>
            doQsoEdit(sessionDao, Some(qso), Some(index))
          case None =>
            Ok(s"Can't find qso with index: $index for $callSign")
        }
      }
  }

  /**
   * Bulk load a bunch of fake qsos for development testing.
   *
   * @param callSign of cabrillo.
   * @param howMany  up to this in the file. See[[ com.wa9nnn.wfdserver.paper.AddMany]]
   * @return
   */
  def addMany(callSign: CallSign, howMany: Int): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      session(callSign) { sessionDao =>
        var count = 0

        addManyOp(2000) { theirCallSign =>
          sessionDao.addQso(PaperLogQso(freq = s"7.$count", time = LocalTime.now().plusMinutes(count), theirCall = theirCallSign, category = "7O", section = "DX", callSign = callSign))
          count += 1
        }
        Redirect(routes.PaperLogController.qsoEditor(callSign))
      }
  }

  def sessionsList(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      (sessions ? SessionList).mapTo[Table].map { table: Table =>
        Ok(views.html.paperLogSessions(table))
      }
  }

  def invalidate(callSign: CallSign): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      (sessions ? InvalidateSession(callSign)).mapTo[Table].map { table: Table =>

        Ok(views.html.paperLogSessions(table))
      }
  }
}

object PaperLogController {
  def editClass(form: Form[PaperLogQso]): String = {
    val str = form.data("index")
    if (str == "-1") {
      ""
    } else {
      "editing"
    }
  }

}

