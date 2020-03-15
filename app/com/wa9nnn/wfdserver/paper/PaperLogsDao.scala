
package com.wa9nnn.wfdserver.paper

import java.nio.file.{Files, Path, Paths}
import java.time.Instant

import com.github.racc.tscg.TypesafeConfig
import com.wa9nnn.wfdserver.auth.WfdSubject
import com.wa9nnn.wfdserver.htmlTable._
import com.wa9nnn.wfdserver.model.{CallSign, PaperLogQso}
import com.wa9nnn.wfdserver.paper.SessionDao.metadata
import controllers.routes
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{Format, Json}

import scala.jdk.StreamConverters._

/**
 *
 * @param paperLogDirectory   where files will live.
 *                            subdirectory named with callSign
 *                            containing:
 *                            user.txt on start edit from WfdSubject
 *                            header.json on 1st com.wa9nnn.wfdserver.paper.PaperLogEditor#saveHeader(com.wa9nnn.wfdserver.paper.PaperLogHeader)
 *                            qsos.txt appeded on each addQso. One line of QSO json per qso.
 *
 */
@Singleton
class PaperLogsDao @Inject()(@TypesafeConfig("wfd.paperLogDirectory") paperLogDirectory: String) {
  def delete(callSign: CallSign): Unit = {
    val callSignDir = directory.resolve(callSign.fileSafe)
    org.apache.commons.io.FileUtils.deleteDirectory(callSignDir.toFile)
  }


  val directory: Path = Paths.get(paperLogDirectory)

  /**
   *
   * @param user if None that for any user
   * @return
   */
  def list(user: Option[String] = None): Seq[PaperLogMetadata] = {
    Files.createDirectories(directory)
    Files.list(directory)
      .toScala(Iterator)
      .filterNot(Files.isHidden)
      .map { callSignPath =>
        metadata(callSignPath)
      }.toSeq
  }

  def table: Table = {
    val metadatas: Seq[PaperLogMetadata] = list()
    Table(PaperLogMetadata.header(metadatas.length), metadatas.map(_.toRow)).withCssClass("resultTable")
  }

  /**
   *
   * @param callSign   potential new paper log.
   * @param wfdSubject who
   * @return Left[PaperLogEditor] if ok to edit Right[PaperLog] if someone else is editing
   */
  def start(callSign: CallSign)(implicit wfdSubject: WfdSubject): SessionDao = {
    val callSignDir = directory.resolve(callSign.fileSafe)
    new SessionDao(callSign, callSignDir)
  }
}

/**
 *
 * @param callSign   who log is for.
 * @param user       who is editing this.
 * @param lastUpdate stamp
 * @param qsoCount   how many entries.
 */
case class PaperLogMetadata(callSign: CallSign, user: String, lastUpdate: Instant, qsoCount: Int) extends Ordered[PaperLogMetadata] with RowSource {
  override def compare(that: PaperLogMetadata): Int = this.callSign compareTo that.callSign

  override def toRow: Row = {
    Row(
      Cell(callSign).withUrl(routes.PaperLogController.start(callSign).url),
      user,
      lastUpdate,
      qsoCount
    )
  }
}

object PaperLogMetadata {
  def header(count: Int): Header = Header(s"CallSigns being edited $count", "CallSign", "User", "Updated", "Qso Count")
}

case class PaperLog(paperLogDetails: PaperLogMetadata, paperLogHeader: PaperLogHeader, qsos: Seq[PaperLogQso]) {
  def callSign: CallSign = paperLogDetails.callSign

  def okToSubmit: Boolean = {
    qsos.nonEmpty && paperLogHeader.isvalid
  }
}

object PaperLogFormat {
  implicit val f: Format[CallSign] = CallSign.callSignFormat
  implicit val qsoFormat: Format[PaperLogQso] = Json.format
  implicit val detailsFormat: Format[PaperLogMetadata] = Json.format
  implicit val headerFormat: Format[PaperLogHeader] = Json.format
}
