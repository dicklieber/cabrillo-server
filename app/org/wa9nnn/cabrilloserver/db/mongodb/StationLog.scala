package org.wa9nnn.cabrilloserver.db.mongodb

import java.time.{Instant, LocalDateTime}
import java.util.UUID

import org.wa9nnn.cabrillo.parsers.Exchange_WFD

case class StationLog(callsign: String,
                      location: String,
                      category: String,
                      categories: Map[String, String],
                      soapBox: Seq[String],
                      email: Option[String],
                      name: String,
                      claimedScore: Int,
                      _id: String = UUID.randomUUID().toString,
                      ingested: Instant = Instant.now())


case class QSO(logId: String, //FK to StationLog
               freq: String,
               mode: String,
               stamp: Instant,
               sent: Exchange_WFD,
               received: Exchange_WFD,
               _id: String = UUID.randomUUID().toString)

