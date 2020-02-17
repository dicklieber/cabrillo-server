
package com.wa9nnn.wfdserver.db

import java.time.Instant

import com.wa9nnn.cabrillo.model.CabrilloTypes.Tag
import com.wa9nnn.cabrillo.model.{CabrilloData, TagValue}
import com.wa9nnn.cabrillo.parsers.QSO_WFD
import com.wa9nnn.wfdserver.model
import com.wa9nnn.wfdserver.model.WfdTypes.CallSign
import com.wa9nnn.wfdserver.model._

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

    implicit def intO(tag: Tag): Option[Int] = {
      cabrilloData.apply(tag).headOption.map(_.body.toInt)
    }

    def callSign: CallSign = cabrilloData.apply("CALLSIGN").head.body

    val stationLog: StationLog = {
     val callCatSect =  CallCatSect(
        callSign = callSign,
        category = s("CATEGORY"),
        arrlSection = s("ARRL-SECTION")
      )
      StationLog(
        callCatSect = callCatSect,
        club = "CLUB",
        certificate = "CERTIFICATE",
        location = s("LOCATION"),
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
        claimedScore = "CLAIMED-SCORE",
        logVersion = s("START-OF-LOG"),
        ingested = Instant.now
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
    model.LogInstance(
      _id = callSign,
      qsoCount = qsos.length,
      qsos = qsos,
      stationLog = stationLog
    )
  }
}