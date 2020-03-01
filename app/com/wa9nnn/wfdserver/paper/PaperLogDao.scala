
package com.wa9nnn.wfdserver.paper

import java.io.{BufferedWriter, OutputStream}
import java.nio.file.{Files, Path, StandardOpenOption}
import java.time.Instant

import com.wa9nnn.wfdserver.auth.WfdSubject
import com.wa9nnn.wfdserver.htmlTable.Table
import com.wa9nnn.wfdserver.model.CallSign._
import com.wa9nnn.wfdserver.model.{CallSign, PaperLogQso}
import com.wa9nnn.wfdserver.paper.PaperLogDao._
import com.wa9nnn.wfdserver.util.JsonLogging
import play.api.libs.json.{Format, Json}

import scala.io.{BufferedSource, Source}
import scala.util.control.Exception.allCatch
import scala.util.{Failure, Using}

/**
 * used to edit one paper log.
 *
 * @param callSign                    who's
 * @param ourDir                      where we can write stuff.
 *                                    subdirectory named with callSign
 *                                    containing:
 *                                    user.txt on start edit from WfdSubject
 *                                    header.json on 1st com.wa9nnn.wfdserver.paper.PaperLogEditor#saveHeader(com.wa9nnn.wfdserver.paper.PaperLogHeader)
 *                                    qsos.txt appended on each addQso. One line of CSV per qso.
 * @param wfdSubject                  who is doing this.
 */
class PaperLogDao(val callSign: CallSign, val ourDir: Path, wfdSubject: WfdSubject) extends JsonLogging {
  Files.createDirectories(ourDir)
  private val headerFile = ourDir.resolve("header.json")
  private val qsosFile = ourDir.resolve("qsos.txt")

  user(ourDir).getOrElse {
    Using(Files.newBufferedWriter(userFile(ourDir))) { bw: BufferedWriter =>
      bw.write(wfdSubject.identifier)
      bw.write('\n')
    }
  }

  def header: PaperLogHeader = {
    Using(Files.newInputStream(headerFile)) { inputStream =>
      Json.parse(inputStream).as[PaperLogHeader]
    }
  }.getOrElse(PaperLogHeader(callSign))

  def saveHeader(paperLogHeader: PaperLogHeader): Unit = {
    Using(Files.newOutputStream(headerFile)) { outputStream: OutputStream =>
      outputStream.write(Json.prettyPrint(Json.toJson(paperLogHeader)).getBytes)
    } match {
      case Failure(e) =>
        logger.error(e.getMessage)
      case _ =>
    }
  }

  /**
   *
   * @param qso new Qso
   * @return all Qsos
   */
  def addQso(qso: PaperLogQso): Unit = {
    Using(Files.newOutputStream(qsosFile,
      StandardOpenOption.WRITE,
      StandardOpenOption.CREATE,
      StandardOpenOption.APPEND)) { outputStream =>
      outputStream.write(qso.toCsvLine.getBytes())
      outputStream.write('\n')
    }
  }

  def qsos(page: Option[Page] = None): Seq[PaperLogQso] = {
    Using(Source.fromFile(qsosFile.toFile)) { bs =>
      bs.getLines().map { line =>
        PaperLogQso.fromCsv(line, callSign)
      }.toSeq
    }.getOrElse(Seq.empty)
  }

  def qsosTable(page: Option[Page] = None): Table = {
    Table(PaperLogQso.header(qsos().length), qsos(page).map(_.toRow)).withCssClass("resultTable")
  }

  def paperLog: PaperLog = {

    val latest = Seq(
      allCatch opt (Files.getLastModifiedTime(userFile(ourDir))),
      allCatch opt (Files.getLastModifiedTime(headerFile)),
      allCatch opt (Files.getLastModifiedTime(qsosFile)),
    )
      .flatten
      .headOption
      .map(_.toInstant)
      .getOrElse(Instant.now)

    val metadata = PaperLogMetadata(callSign, wfdSubject.identifier, latest, qsos().length)
    PaperLog(metadata, header, qsos())

  }

}


object PaperLogDao {

  def userFile(directory: Path): Path = {
    directory.resolve("user.txt")
  }

  def user(directory: Path): Option[String] = {
    Using(Source.fromFile(userFile(directory).toFile)) { bs: BufferedSource =>
      bs.getLines().toSeq.head
    }.toOption
  }

  def metadata(directory: Path): PaperLogMetadata = {
    val callSign = directory.getFileName.toString
    metadata(callSign, directory)
  }

  def metadata(callSign: CallSign, directory: Path): PaperLogMetadata = {
    val qsosfile = directory.resolve("qsos.txt")
    val latest = Seq(
      allCatch opt (Files.getLastModifiedTime(userFile(directory))),
      allCatch opt (Files.getLastModifiedTime(directory)),
      allCatch opt (Files.getLastModifiedTime(directory)),
    )
      .flatten
      .headOption
      .map(_.toInstant)
      .getOrElse(Instant.now)

    val qsoCount = Using(Source.fromFile(qsosfile.toFile)) { br: BufferedSource =>
      br.getLines().length
    }.getOrElse(0)
    PaperLogMetadata(callSign, user(directory).getOrElse("?"), latest, qsoCount)
  }

  implicit val f = CallSign.callSignFormat
  implicit val qsoFormat: Format[PaperLogQso] = Json.format
  implicit val headerFormat: Format[PaperLogHeader] = Json.format

}

case class Page(pageNo: Int, pageSize: Int = 25)