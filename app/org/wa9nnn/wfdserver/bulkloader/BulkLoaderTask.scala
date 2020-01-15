
package org.wa9nnn.wfdserver.bulkloader

import java.nio.file.{Files, Path, Paths}
import java.time.Instant

import akka.actor.Actor
import com.github.racc.tscg.TypesafeConfig
import javax.inject.Inject
import nl.grons.metrics4.scala.DefaultInstrumented
import org.wa9nnn.cabrillo.ResultWithData
import org.wa9nnn.wfdserver.Loader
import org.wa9nnn.wfdserver.model.LogInstance
import org.wa9nnn.wfdserver.play.JsonFormat._
import org.wa9nnn.wfdserver.util.JsonLogging
import play.api.libs.json.Json

import scala.jdk.CollectionConverters._

/**
 * Child actor of BulkLoader
 *
 * @param loader       thank that now how to ingest cabrillo files.
 * @param dir          where to get cabrillo files from.
 */
class BulkLoaderTask @Inject()(loader: Loader, @TypesafeConfig("wfd.bulkLoad.directory") dir: String) extends Actor with DefaultInstrumented with JsonLogging {

  private val bulkLoadMeter = metrics.meter("BulkLoadFile")
  private val buildLoadDir = Paths.get(dir)
  private var started: Option[Instant] = None
  private var count = 0
  private var filesInCount = 0

  def start(): Unit = {
    started = Option(Instant.now)
    val okDir = buildLoadDir.resolve("ok")
    val failedDir = buildLoadDir.resolve("failed")
    Files.createDirectories(okDir)
    Files.createDirectories(failedDir)
    count = 0
    metrics.timer("BulkLoadOperation").time {

      val files: Seq[Path] = Files.newDirectoryStream(buildLoadDir).iterator().asScala.toSeq.flatMap { f =>
        if (Files.isRegularFile(f) && !Files.isHidden(f)) {
          Seq(f)
        } else {
          Seq.empty
        }
      }
      filesInCount = files.length
      context.parent ! StartingBulkLoad(filesInCount)
      logJson("bulkLoader Start").++("inCount" -> filesInCount)
      files.foreach { file =>
        try {
          val (resultWithData: ResultWithData, maybeLogInstance: Option[LogInstance]) = loader(file, "bulkLoader")
          context.parent ! (maybeLogInstance match {
            case Some(logInstance) =>
              val to = okDir.resolve(file.getFileName)
              Files.move(file, to)
              logInstance
            case None =>
              val to = failedDir.resolve(file.getFileName)
              Files.move(file, to)
              val fileName: String = file.getFileName.toFile.getName
              val resultJsonFile = failedDir.resolve(fileName + ".json")
              val json = Json.prettyPrint(Json.toJson(resultWithData.result))
              Files.write(resultJsonFile, json.getBytes)
              FailedOne
          })
          bulkLoadMeter.mark()
          count = count + 1
        } catch {
          case e: Exception =>
            logger.error("bulk loading", e)
        }
      }
    }
    logJson("bulkLoader finish")
      .++("inCount" -> filesInCount,
        "processedCount" -> count)
      .info()
    context.parent ! BulkLoadDone
    context.stop(self)
  }

  override def receive: Receive = {
    case StartBulkLoadRequest =>
      started
        .map(_ => logger.error(s"Bulkload already running since $started."))
        .orElse {
          start()
          None
        }
  }

}


case object StartBulkLoadRequest


case class StartingBulkLoad(nFiles: Int)

case object FailedOne

case object BulkLoadDone