package controllers

import java.nio.file.{Path, Paths}

import javax.inject._
import akka.actor.ActorSystem
import akka.stream.IOResult
import akka.stream.scaladsl.FileIO
import org.wa9nnn.cabrillo.Cabrillo
import play.api.libs.streams.Accumulator
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.io.Source

/**
 * This controller creates an `Action` that demonstrates how to write
 * simple asynchronous code in a controller. It uses a timer to
 * asynchronously delay sending a response for 1 second.
 *
 * @param cc standard controller components
 * @param actorSystem We need the `ActorSystem`'s `Scheduler` to
 * run code after a delay.
 * @param exec We need an `ExecutionContext` to execute our
 * asynchronous code.  When rendering content, you should use Play's
 * default execution context, which is dependency injected.  If you are
 * using blocking operations, such as database or network access, then you should
 * use a different custom execution context that has a thread pool configured for
 * a blocking API.
 */
@Singleton
class WfdController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends AbstractController(cc) {



  def upload = Action(parse.multipartFormData) { request =>
    request.body
      .file("cabrillo")
      .map { picture =>
        // only get the last part of the filename
        // otherwise someone can send a path like ../../home/foo/bar.txt to write to other files on the system
        val filename: Path = Paths.get(picture.filename).getFileName
        val fileSize    = picture.fileSize
        val contentType = picture.contentType

        val url = picture.ref.toURI.toURL

        val bufferedSource = Source.fromURL(url)
        val result = Cabrillo(bufferedSource, url)


        Ok(views.html.wfdresult(result, filename.toString))

        //        picture.ref.copyTo(Paths.get(s"/tmp/picture/$filename"), replace = true)
//        Ok("File uploaded")
      }
      .getOrElse {
        NoContent
      }
  }
}
