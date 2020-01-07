package controllers

import java.nio.file.{Path, Paths}

import akka.actor.ActorSystem
import javax.inject._
import org.wa9nnn.cabrillo.ResultWithData
import org.wa9nnn.wfdserver.Loader
import org.wa9nnn.wfdserver.db.{DbIngestResult, LogInstance}
import org.wa9nnn.wfdserver.htmlTable.Table
import org.wa9nnn.wfdserver.util.JsonLogging
import play.api.libs.Files
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.language.postfixOps
import org.wa9nnn.wfdserver.scoring.{ScoringEngine, ScoringResult}
@Singleton
class WfdController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem, loader: Loader)(implicit exec: ExecutionContext)
  extends AbstractController(cc) with JsonLogging {
  setLoggerName("cabrillo")

  def upload: Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData) { implicit request: Request[MultipartFormData[Files.TemporaryFile]] =>

    request.body
      .file("cabrillo")
      .map { picture =>
        // only get the last part of the filename
        // otherwise someone can send a path like ../../home/foo/bar.txt to write to other files on the system
        val filename: Path = Paths.get(picture.filename).getFileName

        val path = Paths.get(picture.ref.toURI)
        val (resultWithData: ResultWithData, maybeLogEntryId: Option[DbIngestResult]) = loader(path, request.connection.remoteAddress.toString)
        val scoringResultTable: Table = maybeLogEntryId.map {
          case li: LogInstance =>
             ScoringEngine(li).table
        }.getOrElse( Table("Couldn't Score", "") )




        Ok(views.html.wfdresult(resultWithData.result, filename.toString, maybeLogEntryId.map(_.id), scoringResultTable)(request.asInstanceOf[Request[AnyContent]]))

      }
      .getOrElse {
        NoContent
      }
  }


}
