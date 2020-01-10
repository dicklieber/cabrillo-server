package controllers

import be.objectify.deadbolt.scala.ActionBuilders
import javax.inject._
import org.wa9nnn.wfdserver.CallSignId
import org.wa9nnn.wfdserver.db.{DBRouter, EntryViewData}
import org.wa9nnn.wfdserver.db.DBRouter.{dbFromSession, _}
import org.wa9nnn.wfdserver.htmlTable._
import org.wa9nnn.wfdserver.model.LogInstance
import org.wa9nnn.wfdserver.scoring.ScoringEngine
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, _}

import scala.concurrent.Future

/**
 * Administrative pages
 */
@Singleton
class AdminController @Inject()(cc: ControllerComponents,
                                db: DBRouter,
                                actionBuilder: ActionBuilders
                               )
  extends AbstractController(cc)
    with play.api.i18n.I18nSupport {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global


  def callsigns(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request: Request[AnyContent] =>
      request.session.get(org.wa9nnn.wfdserver.db.DBRouter.dbSessionKey).getOrElse()
      val dbName: Option[String] = dbFromSession
      db.callSignIds(dbName).map { callSignIds =>
        val table = MultiColumn(callSignIds.map(_.toCell), 10, s"Submissions (${callSignIds.length})").withCssClass("resultTable")
        Ok(views.html.entries(table, dbName.getOrElse("default")))
      }
  }

  def submission(callsignId: CallSignId): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() { implicit request =>
    val dbName = dbFromSession
    db.logInstance(callsignId.entryId, dbName).map {
      case Some(logInstance) =>
        val stationLog = logInstance.stationLog
        val evd = EntryViewData(logInstance)

        val scoringTable: Table = ScoringEngine(logInstance).table

        Ok(views.html.entry(evd, scoringTable, dbName.getOrElse("default")))
      case None =>
        NotFound("Cannot find ID")
    }
  }

  private val stuffForm: Form[DbName] = Form(
    mapping(
      "dbName" -> text
    )(DbName.apply)(DbName.unapply)
  )

  /**
   *
   * @return landing page for admin (netcontrol)
   */
  def adminlanding(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() { implicit request =>
    val dbName = dbFromSession.getOrElse("")
    Future(Ok(views.html.admin(stuffForm.fill(DbName(dbName)), db, dbName)))
  }

  def stuffPost(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() { implicit request =>
    val userData = stuffForm.bindFromRequest.get
    val dbName = userData.dbName
    Future(Ok(views.html.admin(stuffForm.fill(DbName(dbName)), db, dbName)).withSession(dbToSession(dbName)))
  }

}

case class DbName(dbName: String)

