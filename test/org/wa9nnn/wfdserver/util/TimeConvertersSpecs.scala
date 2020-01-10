package org.wa9nnn.wfdserver.util

import java.sql._
import java.time.{Instant, ZoneId}
import java.time.temporal.{ChronoField, TemporalField}

import org.specs2.mutable.Specification

class TimeConvertersSpecs extends Specification {

  "TimeConverters" >> {
    "instantDisplay" >> {
      val str = TimeConverters.instantDisplayUTCCST(Instant.EPOCH)
      str must beEqualTo("""01/01/70 00:00 UTC (12/31/69 18:00 CST)""")
    }

    "sql round trip" >> {
      val instant = Instant.now() //.`with`(ChronoField.MICRO_OF_SECOND, 0)
      val (date, time: Time) = TimeConverters.instantToSql(instant)
      val zonedUtc = instant.atZone(ZoneId.of("UTC"))
      val millis = instant.toEpochMilli
      val dt = date.getTime
      val lt = time.getTime

      val timestamp = Timestamp.from(instant)
      val backAgain = TimeConverters.sqlToInstant(date, time)
      backAgain must beEqualTo(instant)
    }

  }
}
