
package org.wa9nnn.wfdserver.db.mongodb

import org.wa9nnn.cabrillo.model.CabrilloTypes.Tag
import org.wa9nnn.cabrillo.model.{CabrilloData, TagValue}
import org.wa9nnn.cabrillo.parsers.QSO_WFD
import org.wa9nnn.wfdserver.db._

object LogInstanceAdapter {

  /**
   *
   * Converts a [[CabrilloData]] to a [[LogInstance]].
   *
   * @param cabrilloData as parsed
   */
  def apply(cabrilloData: CabrilloData): LogInstance = {

    /**
     *
     * @param tag name
     * @return body of 1st instance of the [[Tag]] or None if tag not present
     */
    implicit def str(tag: Tag): Option[String] = {
      cabrilloData.apply(tag).headOption.map(_.body)
    }

    implicit def s(tag: Tag): String = {
      cabrilloData.apply(tag).headOption.map(_.body).getOrElse("")
    }

//    implicit def bol(t: (Tag, String)): Option[Boolean] = {
//      cabrilloData.apply(t._1).headOption.map(_.body.toUpperCase() == t._2)
//    }
//
    implicit def intO(tag: Tag): Option[Int] = {
      cabrilloData.apply(tag).headOption.map(_.body.toInt)
    }

    def int(tag: Tag): Int = {
      cabrilloData.apply(tag).headOption.map(_.body.toInt).get
    }

//    implicit def asDate(stamp: Instant): java.sql.Date = {
//      new java.sql.Date(stamp.toEpochMilli)
//    }
//
//    implicit def asTime(stamp: Instant): java.sql.Time = {
//      new java.sql.Time(stamp.toEpochMilli)
//    }

    def callsign: String = cabrilloData.apply("CALLSIGN").head.body

    val stationLog: StationLog = {
      StationLog(
        callSign = callsign,
        club = "CLUB",
        certificate = "CERTIFICATE",
        location = s("LOCATION"),
        category = s("CATEGORY"),
        categories = Categories(
          operator = "CATEGORY-OPERATOR",
          station = "CATEGORY-STATION",
          transmitter = "CATEGORY-TRANSMITTER",
          overlay = "CATEGORY-OVERLAY",
          power = "CATEGORY-POWER",
          assisted = "CATEGORY-ASSISTED",
          band = "CATEGORY-BAND",
          mode = "CATEGORY-MODE"
        ),
        createdBy = "CREATED-BY",
        gridLocator = "GRID-LOCATOR",
        address = cabrilloData("ADDRESS").map(_.body).toList,
        city = "ADDRESS-CITY",
        stateProvince = "ADDRESS-STATE-PROVINCE",
        postalCode = "ADDRESS-POSTALCODE",
        country = "ADDRESS-COUNTRY",
        soapBoxes = for {
          soapbox <- cabrilloData("SOAPBOX").toList
          body = soapbox.body.trim
          if !body.isEmpty
        } yield {
          body
        },
        email = "EMAIL",
        name = "NAME",
        claimedScore = "CLAIMED-SCORE"
      )
    }

    val qsos: Seq[Qso] = {
      cabrilloData("QSO").map { tv: TagValue =>
        val q: QSO_WFD = tv.asInstanceOf[QSO_WFD]
        Qso(
          b = q.freq,
          m = q.mode,
          ts = q.stamp,
          s = Exchange(q.sent),
          r = Exchange(q.received)
        )
      }
    }
    LogInstance(
      logVersion = 0,
      qsoCount = qsos.length,
      qsos = qsos,
      stationLog = stationLog
    )
  }
}