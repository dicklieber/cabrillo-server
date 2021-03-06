package controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.typesafe.config.Config
import javax.inject._
import com.wa9nnn.wfdserver.CabrilloFileManager
import com.wa9nnn.wfdserver.auth.SubjectAccess
import com.wa9nnn.wfdserver.auth.WfdSubject.sessionKey
import com.wa9nnn.wfdserver.db.{DBRouter, EntryViewData}
import com.wa9nnn.wfdserver.htmlTable._
import com.wa9nnn.wfdserver.scoring.ScoringEngine
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
                               )(implicit config: Config)
  extends AbstractController(cc) with SubjectAccess
    with play.api.i18n.I18nSupport {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global


  def callSigns(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request: Request[AnyContent] =>

    db.callSignIds().map { callSignIds =>
      val table = MultiColumn(callSignIds.map(_.toCell), 10, s"Submissions (${callSignIds.length})").withCssClass("resultTable")
      Ok(views.html.entries(table))
    }
  }

  def submission(entryId:String): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() { implicit request =>
    db.logInstance(entryId).map {
      case Some(logInstance) =>
        val entryViewData = EntryViewData(logInstance)
        val filesTable: Table = cabrilloFileManager.table(logInstance.callSign)
        val scoringTable: Table = scoringEngine.provisional(logInstance).table

        val t = Table(Header("nothing"), Seq.empty)
//        Ok(views.html.entry(entryViewData, t, t))
        Ok(views.html.entry(entryViewData, filesTable, scoringTable))
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

