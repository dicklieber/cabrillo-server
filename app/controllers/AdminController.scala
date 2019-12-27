package controllers

import be.objectify.deadbolt.scala.ActionBuilders
import javax.inject._
import org.wa9nnn.wfdserver.CallSignId
import org.wa9nnn.wfdserver.db.DBRouter
import org.wa9nnn.wfdserver.db.DBRouter.dbFromSession
import org.wa9nnn.wfdserver.htmlTable._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, _}
import org.wa9nnn.wfdserver.db.DBRouter._
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
        val table = MultiColumn(callSignIds.map(_.toCell), 10, "Submissions").withCssClass("resultTable")
        Ok(views.html.entries(table, dbName.getOrElse("default")))
      }
  }

  def submission(callsignId: CallSignId): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() { implicit request =>
    val dbName = dbFromSession
    db.entry(callsignId.entryId, dbName).map {
      case Some(entry) =>
        Ok(views.html.entry(entry, dbName.getOrElse("default")))
      case None =>
        NotFound("Cannot find ID")
    }
  }

  private val stuffForm: Form[Stuff] = Form(
    mapping(
      "dbName" -> text
    )(Stuff.apply)(Stuff.unapply)
  )

  /**
   *
   * @return landing page for admin (netcontrol)
   */
  def adminlanding(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() { implicit request =>
    val dbName = dbFromSession.getOrElse("")
    Future(Ok(views.html.admin(stuffForm.fill(Stuff(dbName)), db, dbName)))
  }

  def stuffPost(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() { implicit request =>
    val userData = stuffForm.bindFromRequest.get
    val dbName = userData.dbName
    Future(Ok(views.html.admin(stuffForm.fill(Stuff(dbName)), db, dbName)).withSession(dbToSession(dbName)))
  }

}

case class Stuff(dbName: String)

