package org.wa9nnn.wfdserver.db.mongodb

import java.time.Instant

import org.specs2.mutable.Specification
import org.wa9nnn.wfdserver.htmlTable.TextRenderer

class RowSourceSpec extends Specification {
  implicit def x(s: String): Option[String] = Some(s)

  "RowSource" >> {
    val categories = new Categories(station = Some("WA9NNN"), band = Some("20M"))

    "rows" >> {
      val rows = categories.toRows()
      rows must haveLength(7)

      val str = TextRenderer(rows)
      str mustEqual ("""||category-operator   |      |
                        ||category-station    |WA9NNN|
                        ||category-transmitter|      |
                        ||category-power      |      |
                        ||category-assisted   |      |
                        ||category-band       |20M   |
                        ||category-mode       |      |""".stripMargin)
    }
    "nested" >> {

      val stationLog = StationLog(
        logVersion = 2,
        callSign = "WM9W",
        club = Some("The 220MHz Guys"),
        category = "1H",
        location = "IL",
        categories = categories,
        soapBoxes = Seq(
          "1000000 Being Good Guys",
          "1500 outdoor",
          "1,500 points for not using commercial power."
        ),
        email = "dick@u505.com",
        name = "dick lieber",
        claimedScore = 142,
        ingested = Instant.EPOCH
      )
      val rows = stationLog.toRows()
      rows must haveLength(19)

      val str = TextRenderer(rows)
      str mustEqual ("""||logVersion          |2                                           |
                        ||callSign            |WM9W                                        |
                        ||club                |The 220MHz Guys                             |
                        ||location            |IL                                          |
                        ||category            |1H                                          |
                        ||category-operator   |                                            |
                        ||category-station    |WA9NNN                                      |
                        ||category-transmitter|                                            |
                        ||category-power      |                                            |
                        ||category-assisted   |                                            |
                        ||category-band       |20M                                         |
                        ||category-mode       |                                            |
                        ||soapBoxes           |1000000 Being Good Guys                     |
                        ||soapBoxes           |1500 outdoor                                |
                        ||soapBoxes           |1,500 points for not using commercial power.|
                        ||email               |dick@u505.com                               |
                        ||name                |dick lieber                                 |
                        ||claimedScore        |142                                         |
                        ||ingested            |12/31/69 18:00 CST                          |""".stripMargin)

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
