package controllers

import javax.inject._
import org.wa9nnn.cabrilloserver.db.mysql.MySqlIngester
import org.wa9nnn.cabrilloserver.db.mysql.Tables._
import org.wa9nnn.cabrilloserver.htmlTable._
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class AdminController @Inject()(cc: ControllerComponents, ingester: MySqlIngester) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def admin: Action[AnyContent] = Action {

    val entries: Seq[EntriesRow] = ingester.entries

    val header = Header[EntriesRow]()
    val entryTable = Table(header, entries.map(entry =>
      Row(entry)): _*)
        .withCssClass("resultTable")

    Ok(views.html.entries(entryTable))
  }

}
