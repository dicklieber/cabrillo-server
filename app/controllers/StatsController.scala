
package controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.typesafe.config.Config
import javax.inject._
import com.wa9nnn.wfdserver.auth.SubjectAccess
import com.wa9nnn.wfdserver.db.DBRouter
import com.wa9nnn.wfdserver.util.JsonLogging
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

@Singleton
class StatsController @Inject()(cc: ControllerComponents, actionBuilder: ActionBuilders, db: DBRouter)(implicit exec: ExecutionContext, config:Config)
  extends AbstractController(cc) with JsonLogging with SubjectAccess {

  def index(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() { implicit request =>

    db.stats().map { table =>
      Ok(views.html.stats(table))
    }
  }

}

