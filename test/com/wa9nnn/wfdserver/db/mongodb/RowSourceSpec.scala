package com.wa9nnn.wfdserver.db.mongodb

import java.time.Instant

import org.specs2.mutable.Specification
import com.wa9nnn.wfdserver.htmlTable.TextRenderer
import com.wa9nnn.wfdserver.model.{CallCatSect, Categories, StationLog}

class RowSourceSpec extends Specification {
  implicit def x(s: String): Option[String] = Some(s)

  "RowSource" >> {
    val categories = new Categories(station = Some("WA9NNN"), band = Some("20M"))

    "rows" >> {
      val rows = categories.toRows()
      rows must haveLength(9)

      val str = TextRenderer(rows)
      str mustEqual ("""||category-operator   |      |
                        ||category-station    |WA9NNN|
                        ||category-transmitter|      |
                        ||category-power      |      |
                        ||category-assisted   |      |
                        ||category-overlay    |      |
                        ||category-time       |      |
                        ||category-band       |20M   |
                        ||category-mode       |      |""".stripMargin)
    }
    "nested" >> {

      val stationLog = StationLog(
        callCatSect = CallCatSect( "WM9W","1H", "IL"),
        club = Some("The 220MHz Guys"),
        location = "IL",
        categories = categories,
        soapBoxes = List(
          "1000000 Being Good Guys",
          "1500 outdoor",
          "1,500 points for not using commercial power."
        ),
        email = "dick@u505.com",
        name = "dick lieber",
        claimedScore = Some(142),
        ingested = Instant.EPOCH,
        createdBy = "fdcluster",
        certificate = None,
        address = List.empty,
        city = None,
        stateProvince = None,
        postalCode = None,
        country = None,
        gridLocator = None,
        logVersion = "3.0"

      )
      val rows = stationLog.toRows()
      rows must haveLength(29)

      val str = TextRenderer(rows)
      str mustEqual ("""||callSign            |WM9W                                        |
                        ||category            |1H                                          |
                        ||arrlSection         |IL                                          |
                        ||club                |The 220MHz Guys                             |
                        ||createdBy           |fdcluster                                   |
                        ||location            |IL                                          |
                        ||certificate         |                                            |
                        ||city                |                                            |
                        ||stateProvince       |                                            |
                        ||postalCode          |                                            |
                        ||country             |                                            |
                        ||category-operator   |                                            |
                        ||category-station    |WA9NNN                                      |
                        ||category-transmitter|                                            |
                        ||category-power      |                                            |
                        ||category-assisted   |                                            |
                        ||category-overlay    |                                            |
                        ||category-time       |                                            |
                        ||category-band       |20M                                         |
                        ||category-mode       |                                            |
                        ||soapBoxes           |1000000 Being Good Guys                     |
                        ||soapBoxes           |1500 outdoor                                |
                        ||soapBoxes           |1,500 points for not using commercial power.|
                        ||email               |dick@u505.com                               |
                        ||gridLocator         |                                            |
                        ||name                |dick lieber                                 |
                        ||claimedScore        |142                                         |
                        ||logVersion          |3.0                                         |
                        ||ingested            |01/01/70 00:00 UTC (12/31/69 18:00 CST)     |""".stripMargin)

    }
    "rows skip None" >> {
      val rows = categories.toRows(includeNone = false)
      rows must haveLength(2)

      val str = TextRenderer(rows)
      str mustEqual ("""||category-station|WA9NNN|
                        ||category-band   |20M   |""".stripMargin)
    }

  }
}
