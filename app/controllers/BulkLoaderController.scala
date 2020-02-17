package controllers

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import be.objectify.deadbolt.scala.ActionBuilders
import com.typesafe.config.Config
import javax.inject._
import com.wa9nnn.wfdserver.bulkloader.{BuildLoadStatus, StartBulkLoadRequest, StatusRequest}
import com.wa9nnn.wfdserver.util.JsonLogging
import play.api.mvc.{Action, _}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

@Singleton
class BulkLoaderController @Inject()(cc: ControllerComponents,
                                     actionBuilder: ActionBuilders,
                                     @Named("bulkLoader") bulkLoader: ActorRef)(implicit exec: ExecutionContext, config:Config)
  extends AbstractController(cc) with JsonLogging with play.api.i18n.I18nSupport {
  implicit val timeout: Timeout = Timeout(5 seconds) // needed for `?` below

  def start(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() { implicit request =>

    bulkLoader ! StartBulkLoadRequest

    Future(Redirect(routes.BulkLoaderController.status()))
  }

  def status(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() { implicit request =>

    (bulkLoader ? StatusRequest).mapTo[BuildLoadStatus] map { buildLoadStatus: BuildLoadStatus =>

      Ok(views.html.bulkLoader(buildLoadStatus))
    }
  }
}