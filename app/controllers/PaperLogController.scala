
package controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.typesafe.config.Config
import com.wa9nnn.wfdserver.auth.{SubjectAccess, WfdSubject}
import com.wa9nnn.wfdserver.htmlTable.Table
import com.wa9nnn.wfdserver.model.PaperLogQso
import com.wa9nnn.wfdserver.model.WfdTypes.CallSign
import com.wa9nnn.wfdserver.paper._
import com.wa9nnn.wfdserver.play.EnumPlayUtils.`enum`
import com.wa9nnn.wfdserver.util.JsonLogging
import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.collection.concurrent.TrieMap
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaperLogController @Inject()(cc: ControllerComponents, actionBuilder: ActionBuilders,
                                   sectionValidator: SectionValidator,
                                   categoryValidator: CategoryValidator,
                                   paperLogsDao: PaperLogsDao)
                                  (implicit exec: ExecutionContext, config: Config)
  extends AbstractController(cc)
    with JsonLogging with play.api.i18n.I18nSupport
    with SubjectAccess {


  private val formPaperHeader: Form[PaperLogHeader] = Form(
    mapping(
      "callSign" -> text,
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
        .verifying(sectionValidator.passwordCheckConstraint),
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
      "theirCall" -> nonEmptyText,
      "catSect" -> nonEmptyText,
      "callSign" -> nonEmptyText

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

          val paperQsoForm: Form[PaperLogQso] = formQso.fill(PaperLogQso(callSign))
          Ok(views.html.qsoEditor(paperQsoForm, qsosTable))
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
            Ok(views.html.qsoEditor(paperQsoForm, qsosTable))
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
            val maybeString = formWithErrors.data.get("callSign")
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
            val callSign = formWithErrors.data("callSign")
            BadRequest(views.html.qsoEditor(formWithErrors, paperLog(callSign).qsosTable()))
          },
          paperLogQso => {

            val paperLogDao = paperLog(paperLogQso.callSign)
            paperLogDao.addQso(paperLogQso)
            Ok(views.html.qsoEditor(formQso.fill(paperLogQso.next), paperLogDao.qsosTable()))
          }
        )
      }
  }

}

case class SessionKey(user: String, callSign: CallSign)

object SessionKey {
  def apply(subect: WfdSubject, callSign: CallSign): SessionKey = new SessionKey(subect.identifier, callSign)
}

