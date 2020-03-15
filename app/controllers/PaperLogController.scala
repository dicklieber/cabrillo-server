
package controllers

import java.time.{LocalDate, LocalTime}

import akka.actor.ActorRef
import akka.pattern._
import akka.util.Timeout
import be.objectify.deadbolt.scala.ActionBuilders
import com.typesafe.config.Config
import com.wa9nnn.cabrillo.ResultWithData
import com.wa9nnn.wfdserver.Loader
import com.wa9nnn.wfdserver.auth.{SubjectAccess, WfdSubject}
import com.wa9nnn.wfdserver.htmlTable.Table
import com.wa9nnn.wfdserver.model.{CallSign, LogInstance, PaperLogQso}
import com.wa9nnn.wfdserver.paper._
import com.wa9nnn.wfdserver.scoring.ScoringEngine
import com.wa9nnn.wfdserver.util.{JsonLogging, Page}
import javax.inject.{Inject, Named, Singleton}
import play.api.data.Form
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}

@Singleton
class PaperLogController @Inject()(cc: ControllerComponents, actionBuilder: ActionBuilders,
                                   @Named("paperSessions") sessions: ActorRef,
                                   paperLogsDao: PaperLogsDao,
                                   scoringEngine: ScoringEngine,
                                   loader: Loader,
                                   cabrilloGenerator: CabrilloGenerator,
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

  def paperLogList: Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>

      Future(Ok(views.html.paperLogList(paperLogsDao.table)))
  }

  def create(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      val callSign: String = request.body.asFormUrlEncoded.get.head._2.head

      (sessions ? StartSession(callSign, subject)).mapTo[SessionDao].map { _ =>
        Redirect(routes.PaperLogController.headerEditor(callSign))
      }
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
          doQsoEdit(dao)
        } else {
          val plf: Form[PaperLogHeader] = forms.header.fill(header)
          Ok(views.html.paperLogHeader(Some(callSign), plf))
        }
      }
  }

  def result(callSign: CallSign): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      session(callSign) { dao =>
        val pl: PaperLog = dao.paperLog
        Ok(views.html.paperLogResult(pl))
      }
  }

  def cabrillo(callSign: CallSign, mode:String): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      session(callSign) { dao =>
        mode match {
          case "view" =>
            Ok(cabrilloGenerator.string(dao.paperLog))
          case "download" =>
            Ok(cabrilloGenerator.string(dao.paperLog).getBytes()).withHeaders(
              "Content-Type" -> "text/plain",
              "Content-disposition" -> s"attachment; filename=$callSign.cbr"
            )
          case "submit" =>
            val data = cabrilloGenerator.string(dao.paperLog).getBytes()
            val (resultWithData: ResultWithData, maybeLogEntryId: Option[LogInstance]) = loader.doLoad(data)
            val scoringResultTable: Table = maybeLogEntryId.map {
              li: LogInstance =>
                scoringEngine.provisional(li).table
            }.getOrElse(Table("Couldn't Score", ""))

            Ok(views.html.wfdresult(resultWithData.result, "Paper Log", maybeLogEntryId.map(_.id), scoringResultTable)(request.asInstanceOf[Request[AnyContent]], config: Config))

          case "delete" =>
            paperLogsDao.delete(callSign)
            Redirect(routes.PaperLogController.paperLogList())

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

  def qsoEditor(callSign: CallSign): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      session(callSign) {
        doQsoEdit(_)
      }
  }


  def headerEditor(callSign: CallSign): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      session(callSign) { sessionDao =>
        val plHeader: PaperLogHeader = sessionDao.header
        val form: Form[PaperLogHeader] = forms.header.fill(plHeader)
        Ok(views.html.paperLogHeader(callSign, form))
      }
  }

  /**
   *
   * @param callSign of cabrillo
   * @param f        something that returns a [[Result]]
   * @param subject  who
   * @return the [[Result]] in a [[Future]].
   */
  def session(callSign: CallSign)(f: SessionDao => Result)(implicit subject: WfdSubject): Future[Result] = {
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

          session(callSign) {
            sessionDao =>
              sessionDao.saveHeader(paperLogHeader)
              Redirect(routes.PaperLogController.headerEditor(paperLogHeader.callSign))
          }
        }
      )
  }

  def addQso(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      forms.qso.bindFromRequest.fold(
        (formWithErrors: Form[PaperLogQso]) => {
          val callSign: String = formWithErrors.data("callSign")
          session(callSign) {
            sessionDao =>
              BadRequest(views.html.qsoEditor(CallSign(callSign), formWithErrors, sessionDao.qsosTable(Page.last)))
          }
        },
        paperLogQso => {
          session(paperLogQso.callSign) {
            sessionDao =>
              sessionDao.addQso(paperLogQso)
              doQsoEdit(sessionDao, None, None)
          }
        }
      )
  }

  def deleteQso(index: Int, callSign: CallSign): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      session(callSign) {
        sessionDao =>
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
      session(callSign) {
        sessionDao =>
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
      session(callSign) {
        sessionDao =>
          var count = 0

          addManyOp(2000) {
            theirCallSign =>
              val freq = ManyFreqs.next
              sessionDao.addQso(PaperLogQso(freq = s"$freq", time = LocalTime.now().plusMinutes(count), theirCall = theirCallSign, category = "7O", section = "DX", callSign = callSign))
              count += 1
          }
          Redirect(routes.PaperLogController.qsoEditor(callSign))
      }
  }

  def sessionsList(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      (sessions ? SessionList).mapTo[Table].map {
        table: Table =>
          Ok(views.html.paperLogSessions(table))
      }
  }

  def invalidate(callSign: CallSign): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      (sessions ? InvalidateSession(callSign)).mapTo[Table].map {
        table: Table =>

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

