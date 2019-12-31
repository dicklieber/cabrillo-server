package controllers

import akka.actor.ActorRef
import be.objectify.deadbolt.scala.ActionBuilders
import javax.inject._
import org.wa9nnn.wfdserver.StartBulkLoad
import org.wa9nnn.wfdserver.actor.BulkLoaderActorAnno
import org.wa9nnn.wfdserver.util.JsonLogging
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

@Singleton
class BulkLoaderController @Inject()(cc: ControllerComponents,
                                     actionBuilder: ActionBuilders,
                                     @BulkLoaderActorAnno bulkLoader: ActorRef)(implicit exec: ExecutionContext)
  extends AbstractController(cc) with JsonLogging with play.api.i18n.I18nSupport {

  def index(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() { implicit request =>

    Future {
      bulkLoader ! StartBulkLoad
      Ok(views.html.bulkLoader())
    }
  }

}

