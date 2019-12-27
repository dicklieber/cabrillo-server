
package org.wa9nnn.wfdserver.db.mongodb

import org.bson.types.ObjectId
import org.wa9nnn.cabrillo.model.{CabrilloData, TagValue}
import org.wa9nnn.cabrillo.parsers.QSO_WFD
import org.wa9nnn.wfdserver.db.Adapter

/**
 * Converts a [[CabrilloData]] to
 *
 * @param cabrilloData source.
 */
class MongoAdapter(val cabrilloData: CabrilloData) extends Adapter {
  val logId = new ObjectId()

  def stationLog(logVersion:Int): StationLog = {
    StationLog(
      _id = logId,
      logVersion = logVersion,
      callSign = callsign,
      club = "CLUB",
      location = s("LOCATION"),
      category = s("CATEGORY"),
      categories = Categories(
        operator = "CATEGORY-OPERATOR",
        station = "CATEGORY-STATION",
        transmitter = "CATEGORY-TRANSMITTER",
        power = "CATEGORY-POWER",
        assisted = "CATEGORY-ASSISTED",
        band = "CATEGORY-BAND",
        mode = "CATEGORY-MODE"
      ),
      soapBoxes = for {
        soapbox <- cabrilloData("SOAPBOX")
        body = soapbox.body.trim
        if !body.isEmpty
      } yield {
        body
      },
      email = "EMAIL",
      name = "NAME",
      claimedScore = int("CLAIMED-SCORE")
    )
  }

  def qsos: Seq[QSO] = {
    val r = cabrilloData("QSO").map { tv: TagValue =>
      val q: QSO_WFD = tv.asInstanceOf[QSO_WFD]
      QSO(
        _id = new ObjectId(),
        logId = logId,
        freq = q.freq,
        mode = q.mode,
        stamp = q.stamp,
        sent = q.sent,
        received = q.received
      )
    }
    r
  }
}
