package org.wa9nnn.wfdserver.db

import java.time.Instant

import org.mongodb.scala.bson.ObjectId
import org.wa9nnn.cabrillo.parsers.Exchange_WFD
import org.wa9nnn.cabrillo.requirements.Frequencies
import org.wa9nnn.wfdserver.htmlTable.{Cell, Header, Row, RowSource, RowsSource}
import org.wa9nnn.wfdserver.scoring.ModeBand

/**
 * Object persisted in MongoDB and use for scoring.
 *
 * @param _id if mongo this is ObjectId().toHexString, if MySQL it's the PK int value as a String.
 * @param logVersion incremented each time this callSign's log has been replaced.
 * @param qsoCount   continence to get count of all qsos without having to traverse deeper into the object.
 * @param stationLog everything from cabrillo except the QSOs
 * @param qsos       just the QSOs
 */
case class LogInstance(_id: String = new ObjectId().toHexString,
                       logVersion: Int,
                       qsoCount: Int,
                       stationLog: StationLog,
                       qsos: Seq[Qso]
                      ) extends DbIngestResult {
  override def id: String = _id
}


//object LogInstance {
//  def apply(stationLog: StationLog,
//            qsos: Seq[Qso]): LogInstance = {
//    new LogInstance(qsoCount = qsos.length,
//      qsos = qsos,
//      stationLog = stationLog
//    )
//  }
//}

case class StationLog(
                       callSign: String,
                       club: Option[String],
                       createdBy: Option[String],
                       location: Option[String],
                       arrlSection: Option[String],
                       category: String,
                       certificate: Option[String],
                       address:List[String],
                       city:Option[String],
                       stateProvince:Option[String],
                       postalCode: Option[String],
                       country : Option[String],
                       categories: Categories,
                       soapBoxes: List[String],
                       email: Option[String],
                       gridLocator: Option[String],
                       name: Option[String],
                       claimedScore: Option[Int],
                       ingested: Instant = Instant.now()) extends RowsSource

/**
 * This can be persisted in MongoDB and the field names take up space so this is using the really tiny field names
 *
 * @param b  band as defined in [[Frequencies]]
 * @param m  mode
 * @param ts timestamo
 * @param s  sent
 * @param r  received
 */
case class Qso(
                b: String,
                m: String,
                ts: Instant,
                s: Exchange,
                r: Exchange) extends RowSource {
  def toRow: Row = {
    val rr = Row(b, m, s.toCell, r.toCell, ts)
    rr
  }

  lazy val modeBand: ModeBand = ModeBand(m, Frequencies.check(b))
}

case class Exchange(cs: String, ex: String) {
  def toCell: Cell = Cell(s"${cs.padTo(7, ' ')} $ex").withCssClass("exchange")
}

object Exchange {
  def apply(e: Exchange_WFD): Exchange = {
    new Exchange(e.callsign, s"${e.category} ${e.section}")
  }
}

object Qso {
  def header(qsoCoount: Int): Header = Header(s"QSO ($qsoCoount)", "Freq", "Mode", "Sent", "Received", "Stamp")
}


case class Categories(
                       operator: Option[String] = None,
                       station: Option[String] = None,
                       transmitter: Option[String] = None,
                       power: Option[String] = None,
                       assisted: Option[String] = None,
                       overlay:Option[String]= None,
                       time:Option[String]= None,
                       band: Option[String] = None,
                       mode: Option[String] = None
                     ) extends RowsSource {
  override def toRows(includeNone: Boolean, prefix: String): Seq[Row] = {

    super.toRows(includeNone = includeNone,
      prefix = "category-")

  }
}



