package controllers

import be.objectify.deadbolt.scala.ActionBuilders
import javax.inject._
import org.wa9nnn.wfdserver.CabrilloFileManager
import org.wa9nnn.wfdserver.auth.SubjectAccess
import org.wa9nnn.wfdserver.auth.WfdSubject.sessionKey
import org.wa9nnn.wfdserver.db.{DBRouter, EntryViewData}
import org.wa9nnn.wfdserver.htmlTable._
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
                                cabrilloFileManager: CabrilloFileManager,
                                scoringEngine: ScoringEngine,
                                actionBuilder: ActionBuilders
                               )
  extends AbstractController(cc) with SubjectAccess
    with play.api.i18n.I18nSupport {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global


  def callSigns(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request: Request[AnyContent] =>
      request.session.get(org.wa9nnn.wfdserver.db.DBRouter.dbSessionKey).getOrElse()
      val dbName: Option[String] = dbFromSession
      db.callSignIds(dbName).map { callSignIds =>
        val table = MultiColumn(callSignIds.map(_.toCell), 10, s"Submissions (${callSignIds.length})").withCssClass("resultTable")
        Ok(views.html.entries(table, dbName))
      }
  }

  def submission(callsignId: CallSignId): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() { implicit request =>
    val dbName = dbFromSession
    db.logInstance(callsignId.entryId, dbName).map {
      case Some(logInstance) =>
        val entryViewData = EntryViewData(logInstance)
        val filesTable: Table = cabrilloFileManager.table(logInstance.callSign)
        val scoringTable: Table = scoringEngine.provisional(logInstance).table

        Ok(views.html.entry(entryViewData, filesTable, scoringTable, dbName.getOrElse("default")))
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
   * @return landing page for admin
   */
  def adminlanding(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() { implicit request =>
    val dbName = subject.dbName
    Future(Ok(views.html.admin(stuffForm.fill(DbName(dbName)), db, dbName)))
  }

  def stuffPost(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request: Request[AnyContent] =>

      val userData = stuffForm.bindFromRequest.get
      val dbName = userData.dbName
      val updatedSubject = subject.copy(dbName = dbName)


      val result: Result = Ok(views.html.admin(stuffForm.fill(DbName(dbName)), db, dbName))
      val withNewSubject: Result = result.withSession(sessionKey -> updatedSubject.toJson)
      Future(withNewSubject)
  }

  def recent(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request: Request[AnyContent] =>
      db.recent().map { callSignIds =>
        val table = MultiColumn(callSignIds.map(_.toCell), 10, s"Submissions (${callSignIds.length})").withCssClass("resultTable")
        Ok(views.html.entries(table))
      }
  }
}

case class DbName(dbName: String)

