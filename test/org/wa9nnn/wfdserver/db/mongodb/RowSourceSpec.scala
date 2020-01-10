package org.wa9nnn.wfdserver.db.mongodb

import java.time.Instant

import org.specs2.mutable.Specification
import org.wa9nnn.wfdserver.htmlTable.TextRenderer
import org.wa9nnn.wfdserver.model.{Categories, StationLog}

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
        callSign = "WM9W",
        club = Some("The 220MHz Guys"),
        category = "1H",
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
        arrlSection = Some("IL")

      )
      val rows = stationLog.toRows()
      rows must haveLength(28)

      val str = TextRenderer(rows)
      str mustEqual ("""||callSign            |WM9W                                        |
                        ||club                |The 220MHz Guys                             |
                        ||createdBy           |fdcluster                                   |
                        ||location            |IL                                          |
                        ||arrlSection         |IL                                          |
                        ||category            |1H                                          |
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
                        ||ingested            |01/01/70 00:00 UTC                          |""".stripMargin)

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
