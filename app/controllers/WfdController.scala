package controllers

import java.nio.file.{Path, Paths}
import java.time.Instant

import akka.actor.ActorSystem
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import javax.inject._
import org.slf4j.LoggerFactory
import org.wa9nnn.cabrillo.Cabrillo
import org.wa9nnn.cabrilloserver.FileSaver
import org.wa9nnn.cabrilloserver.util.JsonLogging
import play.api.data.Form
import play.api.libs.Files
import play.api.mvc._

import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.io.{BufferedSource, Source}
import scala.jdk.CollectionConverters._
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
class WfdController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext)
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
        val fileSize = picture.fileSize
        val contentType = picture.contentType

        val url = picture.ref.toURI.toURL

        val ots: Option[Seq[String]] = request.body.asFormUrlEncoded.get("okToSave")
//        val ots: Seq[String] = request.body.dataParts("okToSave")
//        val okToSave: Option[String] = ots.headOption

         val bufferedSource = Source.fromURL(url)
        val result = Cabrillo(bufferedSource, url)
        val email = result.email.getOrElse("missing")

       val ff: Option[Path] =  ots.map { x =>
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

        //        val logger1: Logger = logger
        import ch.qos.logback.classic.LoggerContext
        val context: LoggerContext = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
        val list: mutable.Seq[Logger] = context.getLoggerList.asScala
        for {
          logger <- list
          appender <- logger.iteratorForAppenders.asScala
        } {
          println(s"${logger.getName}:  ${appender.getName}")

        }

        Ok(views.html.wfdresult(result, filename.toString))

        //        picture.ref.copyTo(Paths.get(s"/tmp/picture/$filename"), replace = true)
        //        Ok("File uploaded")
      }
      .getOrElse {
        NoContent
      }
  }


}
