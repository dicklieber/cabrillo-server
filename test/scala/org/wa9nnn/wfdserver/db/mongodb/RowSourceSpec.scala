package org.wa9nnn.wfdserver.db.mongodb

import java.time.Instant

import org.bson.types.ObjectId
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
        _id = new ObjectId("5e054282b4e5dc541c3abf44"),
        callsign = "WM9W",
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
      rows must haveLength(20)

      val str = TextRenderer(rows)
      str mustEqual ("""||_id                 |5e054282b4e5dc541c3abf44                    |
                        ||logVersion          |0                                           |
                        ||callsign            |WM9W                                        |
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
                        ||ingested            |12/31/69 18:00:00 CST                       |""".stripMargin)

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
