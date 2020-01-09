
package controllers


import be.objectify.deadbolt.scala.ActionBuilders
import com.kenshoo.play.metrics.{Metrics, MetricsDisabledException}
import javax.inject.Inject
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Request}

import scala.concurrent.Future

class MetricsController @Inject()(
                                   met: Metrics,
                                   controllerComponents: ControllerComponents,
                                   actionBuilder: ActionBuilders)
  extends AbstractController(controllerComponents) {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  def metrics: Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request: Request[AnyContent] =>
      Future(
        try {
          Ok(met.toJson)
            .as("application/json")
            .withHeaders("Cache-Control" -> "must-revalidate,no-cache,no-store")
        } catch {
          case ex: MetricsDisabledException =>
            InternalServerError("metrics plugin not enabled")
        }
      )
  }
}

