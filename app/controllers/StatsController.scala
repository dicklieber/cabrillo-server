
package controllers

import be.objectify.deadbolt.scala.ActionBuilders
import javax.inject._
import org.wa9nnn.wfdserver.db.mysql.DB
import org.wa9nnn.wfdserver.htmlTable.{Header, Row, Table}
import org.wa9nnn.wfdserver.stats.StatsGenerator
import org.wa9nnn.wfdserver.util.JsonLogging
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

@Singleton
class StatsController @Inject()(cc: ControllerComponents, actionBuilder: ActionBuilders, statsGenerator: StatsGenerator)(implicit exec: ExecutionContext)
  extends AbstractController(cc) with JsonLogging {
  setLoggerName("cabrillo")


  def index(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() { implicit request =>

    statsGenerator().map { (rows: Seq[Row]) =>
      val table: Table = Table(Header("Statistics", "Item", "Value"), rows:_*).withCssClass("resultTable")

     val withLocations = table.copy(rows = table.rows :++ statsGenerator.aggregates())
      Ok(views.html.stats(withLocations))
    }
  }

}

