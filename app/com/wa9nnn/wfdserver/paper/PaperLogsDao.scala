
package com.wa9nnn.wfdserver.paper

import java.nio.file.{Files, Path, Paths}
import java.time.Instant

import com.github.racc.tscg.TypesafeConfig
import com.wa9nnn.wfdserver.auth.WfdSubject
import com.wa9nnn.wfdserver.htmlTable.{Cell, Header, Row, RowSource, Table}
import com.wa9nnn.wfdserver.model.PaperLogQso
import com.wa9nnn.wfdserver.model.WfdTypes.CallSign
import com.wa9nnn.wfdserver.paper.PaperLogDao.metadata
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


  /**
   *
   * @param callSign   potential new paper log.
   * @param wfdSubject who
   * @return Left[PaperLogEditor] if ok to edit Right[PaperLog] if somewone else is editing
   */
  def start(callSign: CallSign)(implicit wfdSubject: WfdSubject): PaperLogDao = {
    val callSignDir = directory.resolve(callSign)
    new PaperLogDao(callSign, callSignDir, wfdSubject)
    //    if (Files.exists(callSignDir)) {
    //      Right(metadata(callSign, callSignDir))
    //    } else {
    //      Left(new PaperLogDao(callSign, callSignDir, wfdSubject))
    //    }
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
  val header: Header = Header("Callsigns being edited", "CallSign", "User", "Updated", "Qso Count")
}


case class PaperLog(paperLogDetails: PaperLogMetadata, paperLogHeader: PaperLogHeader, qsos: Seq[PaperLogQso]){
  def table:Table = Table(PaperLogQso.header(qsos.length), qsos.map(_.toRow)).withCssClass("resultTable")
}


object PaperLogFormat {
  implicit val qsoFormat: Format[PaperLogQso] = Json.format
  implicit val detailsFormat: Format[PaperLogMetadata] = Json.format
  implicit val headerFormat: Format[PaperLogHeader] = Json.format

}
