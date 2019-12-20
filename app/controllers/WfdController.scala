package controllers

import java.nio.file.{Path, Paths}
import java.time.Instant

import akka.actor.ActorSystem
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import javax.inject._
import org.slf4j.LoggerFactory
import org.wa9nnn.cabrillo.{Cabrillo, ResultWithData}
import org.wa9nnn.cabrilloserver.FileSaver
import org.wa9nnn.cabrilloserver.db.mysql.DB
import org.wa9nnn.cabrilloserver.util.JsonLogging
import play.api.data.Form
import play.api.libs.Files
import play.api.mvc._
import scala.language.postfixOps
import scala.collection.mutable
import scala.concurrent.{Await, ExecutionContext}
import scala.io.{BufferedSource, Source}
import scala.concurrent.duration._
import play.api.data._
import play.api.data.Forms._

/**
 * This controller creates an `Action` that demonstrates how to write
 * simple asynchronous code in a controller. It uses a timer to
 * asynchronously delay sending a response for 1 second.
 *
 * @param cc          standard controller components
 * @param actorSystem We need the `ActorSystem`'s `Scheduler` to
 *                    run code after a delay.
 * @param exec        We need an `ExecutionContext` to execute our
 *                    asynchronous code.  When rendering content, you should use Play's
 *                    default execution context, which is dependency injected.  If you are
 *                    using blocking operations, such as database or network access, then you should
 *                    use a different custom execution context that has a thread pool configured for
 *                    a blocking API.
 */
@Singleton
class WfdController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem, ingester: DB)(implicit exec: ExecutionContext)
  extends AbstractController(cc) with JsonLogging {
  setLoggerName("cabrillo")
  private val fileSaver = new FileSaver(Paths.get("/var/cabrillo"))

  def upload: Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData) { request =>
    request.body
      .file("cabrillo")
      .map { picture =>
        // only get the last part of the filename
        // otherwise someone can send a path like ../../home/foo/bar.txt to write to other files on the system
        val filename: Path = Paths.get(picture.filename).getFileName
//        val fileSize = picture.fileSize
//        val contentType = picture.contentType

        val url = picture.ref.toURI.toURL

        val ots: Option[Seq[String]] = request.body.asFormUrlEncoded.get("okToSave")

        val bufferedSource = Source.fromURL(url)
        val resultWithData: ResultWithData = Cabrillo(bufferedSource)
        val result = resultWithData.result
        val email = result.email.getOrElse("missing")

        val ff: Option[Path] = ots.map { x =>
          val bufferedSource: BufferedSource = Source.fromURL(url)
          fileSaver(bufferedSource, email)
        }


        logJson("result")
          .++("email" -> email)
          .++("stamp" -> Instant.now())
          .++("from" -> request.connection.remoteAddress)
          .++("fileOk" -> (result.tagsWithErrors == 0))
          .++("lines" -> result.linesInFile)
          .++("errorTags" -> result.tagsWithErrors.size)
          .++("unknownTags" -> result.unknownTags.size)
          .++("duration" -> result.duration)
          .++("saveTo" -> ff.getOrElse("declined"))
          .info()

        val autoEntryId: Option[Int] = resultWithData.goodData.map { data =>
          val id = ingester(data)
          logger.info(s"id: $id")
          id
        }


        Ok(views.html.wfdresult(result, filename.toString, autoEntryId))

      }
      .getOrElse {
        NoContent
      }
  }


}
