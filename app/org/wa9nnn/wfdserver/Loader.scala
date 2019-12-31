
package org.wa9nnn.wfdserver

import java.nio.file.{Files, Path}
import java.time.Instant

import javax.inject.{Inject, Singleton}
import nl.grons.metrics4.scala.DefaultInstrumented
import org.wa9nnn.cabrillo.{Cabrillo, ResultWithData}
import org.wa9nnn.wfdserver.db.DBRouter
import org.wa9nnn.wfdserver.util.JsonLogging

/**
 * Parse, check and if OK ingests a file
 */
@Singleton
class Loader @Inject()(fileSaver: FileSaver, db: DBRouter) extends DefaultInstrumented with JsonLogging {
  private val loadMeter = metrics.meter("loadMeter")
  private val qsosMeter = metrics.meter("qsosMeter")

  /**
   *
   * @param path of file.
   * @return ResultWithData -> logEntryId
   */
  def apply(path: Path, from: String): (ResultWithData, Option[String]) = {
    metrics.timer("Load").time {
      loadMeter.mark()
      val rawFile: Array[Byte] = Files.readAllBytes(path)
      val resultWithData: ResultWithData = Cabrillo(rawFile)
      val result = resultWithData.result
      val saveToPath = fileSaver(rawFile, result.callSign.getOrElse("missing"))

      logJson("result")
        .++("email" -> result.callSign.getOrElse("missing"))
        .++("stamp" -> Instant.now())
        .++("from" -> from)
        .++("fileOk" -> (result.tagsWithErrors == 0))
        .++("lines" -> result.linesInFile)
        .++("errorTags" -> result.tagsWithErrors.size)
        .++("unknownTags" -> result.unknownTags.size)
        .++("duration" -> result.duration)
        .++("saveTo" -> saveToPath.toString)
        .info()

      val logEntryId: Option[String] = resultWithData.goodData.map { data =>
        qsosMeter.mark(resultWithData.result.qsoCount)
        db.ingest(data)
      }
      resultWithData -> logEntryId
    }
  }
}
