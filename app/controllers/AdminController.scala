package controllers

import javax.inject._
import org.wa9nnn.cabrilloserver.CallSignId
import org.wa9nnn.cabrilloserver.db.mysql.DB
import org.wa9nnn.cabrilloserver.db.mysql.Tables._
import org.wa9nnn.cabrilloserver.htmlTable._
import play.api.mvc.{Action, _}

/**
 * Administrative pages
 */
@Singleton
class AdminController @Inject()(cc: ControllerComponents, db: DB) extends AbstractController(cc) {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def allEntries: Action[AnyContent] = Action {

    val header = Header[EntriesRow]()
    val entryTable = Table(header, db.entries.map(entry =>
      Row(entry)): _*)
      .withCssClass("resultTable")

    Ok(views.html.entries(entryTable))
  }

  def callsigns(): Action[AnyContent] = Action {
    val table = MultiColumn(db.callSignIds.map(_.toCell), 10, "Submissions").withCssClass("resultTable")
    Ok(views.html.entries(table))
  }

  def submission(callsignId: CallSignId): Action[AnyContent] = Action.async {
    db.entry(callsignId.entryId).map {
      case Some(entry) =>
        Ok(views.html.entry(entry))
      case None =>
        NotFound("Cannot find ID")
    }
  }
}


