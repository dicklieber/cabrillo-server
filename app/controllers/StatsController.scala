
package controllers

import be.objectify.deadbolt.scala.ActionBuilders
import javax.inject._
import org.wa9nnn.wfdserver.db.DBRouter
import org.wa9nnn.wfdserver.util.JsonLogging
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.language.postfixOps
import org.wa9nnn.wfdserver.db.DBRouter.dbFromSession

@Singleton
class StatsController @Inject()(cc: ControllerComponents, actionBuilder: ActionBuilders, db: DBRouter)(implicit exec: ExecutionContext)
  extends AbstractController(cc) with JsonLogging {

  def index(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() { implicit request =>

    val dbName = dbFromSession
    db.stats(dbName).map { table =>
      Ok(views.html.stats(table, dbName.getOrElse("default")))
    }
  }

}

