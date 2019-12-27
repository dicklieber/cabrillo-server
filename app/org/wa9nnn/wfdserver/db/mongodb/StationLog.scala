package org.wa9nnn.wfdserver.db.mongodb

import java.time.Instant

import org.mongodb.scala.bson.ObjectId
import org.wa9nnn.cabrillo.parsers.Exchange_WFD
import org.wa9nnn.wfdserver.htmlTable.{Header, Row, RowsSource, RowSource}

case class StationLog(_id: ObjectId = new ObjectId(), // PK
                      logVersion: Int = 0,
                      callSign: String,
                      club: Option[String],
                      location: String,
                      category: String,
                      categories: Categories,
                      soapBoxes: Seq[String],
                      email: Option[String],
                      name: Option[String],
                      claimedScore: Int,
                      ingested: Instant = Instant.now()) extends RowsSource


case class QSO(
                _id: ObjectId = new ObjectId(), //PK
                logId: ObjectId, //FK to StationLog
                freq: String,
                mode: String,
                stamp: Instant,
                sent: Exchange_WFD,
                received: Exchange_WFD) extends RowSource {
  def toRow: Row = {
    Row(freq, mode, sent, received, stamp)
  }
}

object QSO {
  val header: Header = Header("QSO", "Freq", "Mode", "Sent", "Received", "Stamp")
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



