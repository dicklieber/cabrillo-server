package org.wa9nnn.wfdserver.db.mongodb

import java.time.Instant

import org.mongodb.scala.bson.ObjectId
import org.wa9nnn.cabrillo.model.Exchange
import org.wa9nnn.cabrillo.parsers.Exchange_WFD
import org.wa9nnn.cabrillo.requirements.Frequencies
import org.wa9nnn.wfdserver.db.DbIngestResult
import org.wa9nnn.wfdserver.db.mysql.Band
import org.wa9nnn.wfdserver.htmlTable.{Header, Row, RowSource, RowsSource}
import org.wa9nnn.wfdserver.scoring.ModeBand

/**
 * Object persisted in MongoDB
 * @param _id PK
 * @param qsoCount continence to get count of all qsos without having to traverse deeper into the object.
 * @param stationLog everything from cabrillo except the QSOs
 * @param qsos just the QSOs
 */
case class LogInstance(_id: ObjectId = new ObjectId(),
                       qsoCount: Int,
                       stationLog: StationLog,
                       qsos: Seq[QSO]
                      ) extends DbIngestResult {
  override def id: String = _id.toHexString
}


object LogInstance {
  def apply(stationLog: StationLog,
            qsos: Seq[QSO]): LogInstance = {
    new LogInstance(qsoCount = qsos.length,
      qsos = qsos,
      stationLog = stationLog
    )
  }
}

case class StationLog(
                       logVersion: Int,
                       callSign: String,
                       club: Option[String],
                       location: String,
                       category: String,
                       categories: Categories,
                       soapBoxes: List[String],
                       email: Option[String],
                       name: Option[String],
                       claimedScore: Int,
                       ingested: Instant = Instant.now()) extends RowsSource


case class QSO(
                freq: String,
                mode: String,
                stamp: Instant,
                sent: Exchange_WFD,
                received: Exchange_WFD) extends RowSource {
  def toRow: Row = {
    Row(freq, mode, sent, received, stamp)
  }
  lazy val modeBand: ModeBand =  ModeBand(mode, Frequencies.check(freq))
}


object QSO {
  def header(qsoCoount:Int): Header = Header(s"QSO ($qsoCoount)", "Freq", "Mode", "Sent", "Received", "Stamp")
}


case class Categories(
                       operator: Option[String] = None,
                       station: Option[String] = None,
                       transmitter: Option[String] = None,
                       power: Option[String] = None,
                       assisted: Option[String] = None,
                       band: Option[String] = None,
                       mode: Option[String] = None
                     ) extends RowsSource {
  override def toRows(includeNone: Boolean, prefix: String): Seq[Row] = {

    super.toRows(includeNone = includeNone,
      prefix = "category-")

  }
}



