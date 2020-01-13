
package org.wa9nnn.wfdserver

import java.nio.file.{Files, Path}
import java.time.Instant

import javax.inject.{Inject, Singleton}
import nl.grons.metrics4.scala.DefaultInstrumented
import org.wa9nnn.cabrillo.{Cabrillo, Result, ResultWithData}
import org.wa9nnn.wfdserver.db.{DBRouter, LogInstanceAdapter}
import org.wa9nnn.wfdserver.model.LogInstance
import org.wa9nnn.wfdserver.util.JsonLogging

/**
 * Parse, check and if OK ingests a file
 */
@Singleton
class Loader @Inject()(fileSaver: CabrilloFileManager, db: DBRouter) extends DefaultInstrumented with JsonLogging {
  setLoggerName("cabrillo")

  private val loadMeter = metrics.meter("loadMeter")
  private val qsosMeter = metrics.meter("qsosMeter")

  /**
   *
   * @param path of file.
   * @return ResultWithData -> LogInstance
   * @throws NoCallSignException if no callSign in file.
   */
  def apply(path: Path, from: String): (ResultWithData, Option[LogInstance]) = {
    metrics.timer("Load").time {
      val rawFile: Array[Byte] = Files.readAllBytes(path)
      val resultWithData: ResultWithData = Cabrillo(rawFile)
      val result: Result = resultWithData.result
      val callSign = if (result.callSign.isEmpty) {
        logger.error("NoCallSign")
        throw new NoCallSignException
      } else {
        result.callSign.get
      }
      val saveToPath = fileSaver.save(rawFile, callSign)

      logJson("result")
        .++("callSign" -> callSign)
        .++("stamp" -> Instant.now())
        .++("from" -> from)
        .++("fileOk" -> (result.tagsWithErrors == 0))
        .++("lines" -> result.linesInFile)
        .++("size" -> rawFile.length)
        .++("errorTags" -> result.tagsWithErrors.size)
        .++("unknownTags" -> result.unknownTags.size)
        .++("duration" -> result.duration)
        .++("saveTo" -> saveToPath.toString)
        .info()

      val maybeLogInstance: Option[LogInstance] = resultWithData.goodData.map { cabrilloData =>
        qsosMeter.mark(resultWithData.result.qsoCount)
        val logInstance: LogInstance = LogInstanceAdapter(cabrilloData)

        val li: LogInstance = db.ingest(logInstance)
        li
      }
      loadMeter.mark()
      resultWithData -> maybeLogInstance
    }
  }
}
