
package controllers

import java.time.LocalTime

import akka.stream.scaladsl.JavaFlowSupport.Source
import be.objectify.deadbolt.scala.ActionBuilders
import com.typesafe.config.Config
import com.wa9nnn.wfdserver.auth.{SubjectAccess, WfdSubject}
import com.wa9nnn.wfdserver.htmlTable.Table
import com.wa9nnn.wfdserver.model.CallSign.callSign
import com.wa9nnn.wfdserver.model.{CallSign, PaperLogQso}
import com.wa9nnn.wfdserver.paper._
import com.wa9nnn.wfdserver.play.EnumPlayUtils.`enum`
import com.wa9nnn.wfdserver.util.JsonLogging
import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.collection.concurrent.TrieMap
import scala.concurrent.{ExecutionContext, Future}
import scala.io.{BufferedSource, Source}
import scala.io.Source
import scala.util.{Failure, Success, Try, Using}

@Singleton
class PaperLogController @Inject()(cc: ControllerComponents, actionBuilder: ActionBuilders,
                                   sectionValidator: SectionValidator,
                                   categoryValidator: CategoryValidator,
                                   paperLogsDao: PaperLogsDao)
                                  (implicit exec: ExecutionContext, config: Config,
                                   addMany: AddMany)
  extends AbstractController(cc)
    with JsonLogging with play.api.i18n.I18nSupport
    with SubjectAccess {
  private val formPaperHeader: Form[PaperLogHeader] = Form(
    mapping(
      "callSign" -> callSign,
      "club" -> text,
      "name" -> text,
      "email" -> nonEmptyText,
      "address" -> nonEmptyText,
      "city" -> nonEmptyText,
      "stateProvince" -> nonEmptyText,
      "postalCode" -> nonEmptyText,
      "country" -> nonEmptyText,
      "category" -> nonEmptyText
        .verifying(categoryValidator.categoryConstraint),
      "section" -> nonEmptyText
        .verifying(sectionValidator.sectionConstraint),
      "txPower" -> enum(TxPower),
      "noMainPower" -> boolean,
      "awayFromHome" -> boolean,
      "outdoors" -> boolean,
      "satellite" -> boolean
    )(PaperLogHeader.apply)(PaperLogHeader.unapply)
  )

  private val formQso: Form[PaperLogQso] = Form(
    mapping(
      "freq" -> nonEmptyText,
      "mode" -> nonEmptyText,
      "date" -> localDate("MM/dd/yy"),
      "time" -> localTime("HH:mm"),
      "theirCall" -> callSign,
      "cat" -> nonEmptyText
        .verifying(categoryValidator.categoryConstraint),
      "sect" -> nonEmptyText
        .verifying(sectionValidator.sectionConstraint),
      "callSign" -> callSign

    )(PaperLogQso.apply)(PaperLogQso.unapply))

  private val byCallSign = new TrieMap[CallSign, PaperLogDao]()

  private def paperLog(callSign: CallSign)(implicit request: Request[AnyContent]): PaperLogDao = {
    byCallSign.getOrElseUpdate(callSign, paperLogsDao.start(callSign))
  }

  def index(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      Future {
        val table = Table(com.wa9nnn.wfdserver.paper.PaperLogMetadata.header, paperLogsDao.list().map(_.toRow)).withCssClass("resultTable")
        Ok(views.html.paperLogList(table))
        //        Ok(views.html.paperLogList(table))
      }
  }

  def paperLogList: Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      Future {
        val table = Table(com.wa9nnn.wfdserver.paper.PaperLogMetadata.header, paperLogsDao.list().map(_.toRow)).withCssClass("resultTable")
        Ok(views.html.paperLogList(table))
      }
  }

  def create(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      val callSign = request.body.asFormUrlEncoded.get.head._2.head
      val pl: PaperLog = paperLog(callSign).paperLog

      val paperHeaderForm: Form[PaperLogHeader] = formPaperHeader.fill(pl.paperLogHeader)
      val paperQsoForm: Form[PaperLogQso] = formQso.fill(PaperLogQso(callSign = callSign))
      Future(Ok(views.html.paperLogHeader(Some(callSign), paperHeaderForm)))
  }

  /**
   * from click on callsign.
   *
   * @param callSign of interest.
   */
  def start(callSign: CallSign): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      val dao = paperLog(callSign)
      val pl: PaperLog = dao.paperLog
      val header = pl.paperLogHeader
      Future(
        if (header.isvalid) {
          val plf: Form[PaperLogHeader] = formPaperHeader.fill(header)
          Ok(views.html.paperLogHeader(Some(callSign), plf))
        } else {
          val qsosTable = dao.qsosTable()

          val paperQsoForm: Form[PaperLogQso] = formQso.fill(PaperLogQso(callSign = callSign))
          Ok(views.html.qsoEditor(callSign, paperQsoForm, qsosTable))
        }
      )
  }

  def qsoEditor(callSign: Option[CallSign]): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      Future {
        callSign match {
          case Some(cs) =>
            val qsosTable = paperLog(cs).qsosTable()

            val paperLogQso = PaperLogQso(callSign = cs)
            val paperQsoForm: Form[PaperLogQso] = formQso.fill(paperLogQso)
            Ok(views.html.qsoEditor(callSign, paperQsoForm, qsosTable))
          case None =>
            Ok("Please select or create a call sign in the logs tab.")
        }
      }
  }

  def headerEditor(callSign: Option[CallSign]): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      Future {
        callSign match {
          case Some(cs) =>
            val plHeader: PaperLogHeader = paperLog(cs).header
            val form: Form[PaperLogHeader] = formPaperHeader.fill(plHeader)
            Ok(views.html.paperLogHeader(callSign, form))
          case None =>
            Ok("Please select or create a call sign in the logs tab.")
        }
      }
  }

  def updateHeader(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>

      Future {
        //        val paperQsoForm: Form[PaperLogQso] = formQso.fill(PaperLogQso())
        formPaperHeader.bindFromRequest.fold(
          formWithErrors => {
            val maybeString = Option(formWithErrors.data.get("callSign")
              .map(CallSign(_))
              .getOrElse(CallSign.empty))
            BadRequest(views.html.paperLogHeader(maybeString, formWithErrors))
          },
          paperLogHeader => {
            val paperLogDao = paperLog(paperLogHeader.callSign)
            paperLogDao.saveHeader(paperLogHeader)
            Redirect(routes.PaperLogController.headerEditor(Option(paperLogHeader.callSign)))
          }
        )
      }
  }

  def addQso(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      Future {
        //        val paperHeaderForm: Form[PaperLogHeader] = formPaperHeader.fill(PaperLogHeader("WA9NNN"))
        formQso.bindFromRequest.fold(
          formWithErrors => {
            val callSign: String = formWithErrors.data("callSign")
            BadRequest(views.html.qsoEditor(CallSign(callSign), formWithErrors, paperLog(callSign).qsosTable()))
          },
          paperLogQso => {

            val paperLogDao = paperLog(paperLogQso.callSign)
            paperLogDao.addQso(paperLogQso)
            Ok(views.html.qsoEditor(paperLogDao.callSign, formQso.fill(paperLogQso.next), paperLogDao.qsosTable()))
          }
        )
      }
  }

  def addMany(callSign: String, howMany: Int): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      Future {
        val cs = CallSign(callSign)
        val paperLogDao = paperLog(cs)
        var count = 0


        addMany(2000){ theirCallsign =>
          paperLogDao.addQso(PaperLogQso(freq = s"7.$count", time = LocalTime.now().plusMinutes(count), theirCall = theirCallsign, category = "7O", section = "DX", callSign = cs))

        }

//        val rr: Try[Unit] = Using {
//          val r: BufferedSource = scala.io.Source.fromFile("/Users/dlieber/dev/ham/wfdcheck/conf/SampleCallSigns.txt")
//          r
//        } { bs: BufferedSource =>
//          bs.getLines.foreach { c: String =>
//            val theirCllsign = CallSign(c)
//            paperLogDao.addQso(PaperLogQso(freq = s"7.$count", time = LocalTime.now().plusMinutes(count), theirCall = theirCllsign, category = "7O", section = "DX", callSign = cs))
//            count = count + 1
//            if (count >= howMany) {
//              throw new Exception()
//            }
//          }
//        }
//        rr match {
//          case Failure(exception) =>
//            exception.printStackTrace()
//          case Success(value) =>
//            println(value)
//        }
        Redirect(routes.PaperLogController.qsoEditor(cs))

      }
  }


}

case class SessionKey(user: String, callSign: CallSign)

object SessionKey {
  def apply(subect: WfdSubject, callSign: CallSign): SessionKey = new SessionKey(subect.identifier, callSign)
}

