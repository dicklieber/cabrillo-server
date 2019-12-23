package controllers

import be.objectify.deadbolt.scala.ActionBuilders
import javax.inject._
import org.wa9nnn.wfdserver.CallSignId
import org.wa9nnn.wfdserver.db.mysql.Tables._
import org.wa9nnn.wfdserver.db.mysql.{DB, Tables}
import org.wa9nnn.wfdserver.htmlTable.{Header, _}
import play.api.mvc.{Action, _}

/**
 * Administrative pages
 */
@Singleton
class AdminController @Inject()(cc: ControllerComponents, db: DB, actionBuilder: ActionBuilders) extends AbstractController(cc) {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global


  def callsigns(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request: Request[AnyContent] =>
    db.callSignIds.map { callSignIds =>
      val table = MultiColumn(callSignIds.map(_.toCell), 10, "Submissions").withCssClass("resultTable")
      Ok(views.html.entries(table))
    }
  }

  def submission(callsignId: CallSignId): Action[AnyContent] = Action.async { implicit request =>
    db.entry(callsignId.entryId).map {
      case Some(entry) =>
        Ok(views.html.entry(entry))
      case None =>
        NotFound("Cannot find ID")
    }
  }
}


