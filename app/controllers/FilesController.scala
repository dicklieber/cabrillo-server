
package controllers

import be.objectify.deadbolt.scala.ActionBuilders
import javax.inject.{Inject, Singleton}
import com.wa9nnn.wfdserver.CabrilloFileManager
import play.api.mvc._

import scala.concurrent.Future
import scala.util.{Failure, Success}

@Singleton
class FilesController @Inject()(cc: ControllerComponents,
                                cabrilloFileManager: CabrilloFileManager,
                                actionBuilder: ActionBuilders
                               ) extends AbstractController(cc)
  with play.api.i18n.I18nSupport {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  /**
   *
   * @param key as returned from a [[com.wa9nnn.wfdserver.util.FileInfo]]
   * @return
   */
  def download(key: String): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request: Request[AnyContent] =>

      Future {
        cabrilloFileManager.read(key) match {
          case Failure(_) =>
            NotFound
          case Success(bytes) =>
            Ok(bytes).withHeaders(
              "Content-Type" -> "text/plain",
              "Content-disposition" -> s"attachment; filename=$key"
            )
        }
      }
  }
}
