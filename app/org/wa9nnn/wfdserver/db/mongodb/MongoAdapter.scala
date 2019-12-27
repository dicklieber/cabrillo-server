
package org.wa9nnn.wfdserver.db.mongodb

import org.bson.types.ObjectId
import org.wa9nnn.cabrillo.model.{CabrilloData, TagValue}
import org.wa9nnn.cabrillo.parsers.QSO_WFD
import org.wa9nnn.wfdserver.db.Adapter

/**
 *
 * Converts a [[CabrilloData]] to a [[LogInstance]].
 *
 * @param cabrilloData as parsed
 * @param logVersion   how many time this callsign has been updafted
 */
class MongoAdapter(val cabrilloData: CabrilloData, logVersion: Int) extends Adapter {

  val logId = new ObjectId()

  val stationLog: StationLog = {
    StationLog(
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

  private val qsos: Seq[QSO] = {
    cabrilloData("QSO").map { tv: TagValue =>
      val q: QSO_WFD = tv.asInstanceOf[QSO_WFD]
      QSO(
        freq = q.freq,
        mode = q.mode,
        stamp = q.stamp,
        sent = q.sent,
        received = q.received
      )
    }
  }

  val logInstance: LogInstance = LogInstance(
    stationLog = stationLog,
    qsos = qsos
  )
}
