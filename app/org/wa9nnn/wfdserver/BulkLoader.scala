
package org.wa9nnn.wfdserver

import java.nio.file.{Files, Paths}

import _root_.play.api.libs.json._
import akka.actor.{Actor, Props}
import com.typesafe.config.Config
import javax.inject.{Inject, Singleton}
import nl.grons.metrics4.scala.DefaultInstrumented
import org.wa9nnn.wfdserver.play.JsonFormat._
import org.wa9nnn.wfdserver.util.JsonLogging

import scala.jdk.CollectionConverters._

/**
 * Loads a directory of cabrillo files.
 */
class BulkLoader (loader: Loader, config: Config) extends Actor with DefaultInstrumented with JsonLogging{
  private val dir = Paths.get(config.getString("wfd.bulkLoad.directory"))
  private val bulkLoadMeter = metrics.meter("BulkLoadFile")

  def apply(): Unit = {
    val okDir = dir.resolve("ok")
    val failedDir = dir.resolve("failed")
    Files.createDirectories(okDir)
    Files.createDirectories(failedDir)
    metrics.timer("BulkLoadOperation").time {
      Files.walk(dir)
        .iterator().asScala
        .filter(Files.isRegularFile(_))
        .foreach { file =>
          try {
            val (resultWithData, maybeLogEntryId) = loader(file, "bulkLoader")
            maybeLogEntryId match {
              case Some(_) =>
                val to = okDir.resolve(file.getFileName)
                Files.move(file, to)
              case None =>
                val to = failedDir.resolve(file.getFileName)
                Files.move(file, to)
                val fileName: String = file.getFileName.toFile.getName
                val resultJsonFile = failedDir.resolve(fileName + ".json")
                Files.writeString(resultJsonFile, Json.prettyPrint(Json.toJson(resultWithData.result)))
            }
            bulkLoadMeter.mark()
          } catch {
            case e:Exception =>
              logger.error("bulk loading", e)
          }
        }
    }
  }

  override def receive: Receive = {
    case StartBulkLoad =>
      apply()
  }
}

object BulkLoader {
  def props(loader: Loader, config: Config): Props = {
    Props(new BulkLoader(loader, config))
  }
}

case object StartBulkLoad