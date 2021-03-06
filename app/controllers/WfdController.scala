package controllers

import java.nio.file.{Path, Paths}

import akka.actor.ActorSystem
import com.typesafe.config.Config
import javax.inject._
import nl.grons.metrics4.scala.DefaultInstrumented
import com.wa9nnn.cabrillo.ResultWithData
import com.wa9nnn.wfdserver.Loader
import com.wa9nnn.wfdserver.htmlTable.Table
import com.wa9nnn.wfdserver.model.LogInstance
import com.wa9nnn.wfdserver.recaptcha.RecaptchaSupport
import com.wa9nnn.wfdserver.scoring.ScoringEngine
import com.wa9nnn.wfdserver.util.JsonLogging
import play.api.libs.Files
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

@Singleton
class WfdController @Inject()( cc: ControllerComponents,
                              actorSystem: ActorSystem,
                              loader: Loader,
                              scoringEngine: ScoringEngine,
                              recaptchaSupport: RecaptchaSupport)(implicit exec: ExecutionContext, config: Config)
  extends AbstractController(cc) with JsonLogging with DefaultInstrumented {
  private val timer = metrics.timer("WfdController")

  def upload: Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData) { implicit request: Request[MultipartFormData[Files.TemporaryFile]] =>

    timer.time {
      val body = request.body
      val captchatoken: String = request.body.dataParts.get("captchatoken").head.head

      if (recaptchaSupport(captchatoken)) {
     val r: Option[Result] =    body.file("cabrillo")
          .map { picture =>
            // only get the last part of the filename
            // otherwise someone can send a path like ../../home/foo/bar.txt to write to other files on the system
            val filename: Path = Paths.get(picture.filename).getFileName

            val path = Paths.get(picture.ref.toURI)
            val (resultWithData: ResultWithData, maybeLogEntryId: Option[LogInstance]) = loader(path, request.connection.remoteAddress.toString)
            val scoringResultTable: Table = maybeLogEntryId.map {
              li: LogInstance =>
                scoringEngine.provisional(li).table
            }.getOrElse(Table("Couldn't Score", ""))

            Ok(views.html.wfdresult(resultWithData.result, filename.toString, maybeLogEntryId.map(_.id), scoringResultTable)(request.asInstanceOf[Request[AnyContent]], config: Config))
          }
        r.getOrElse(Ok("Why are we here? todo"))

      } else {
        Ok("scored poorly")
      }
    }
  }
}
