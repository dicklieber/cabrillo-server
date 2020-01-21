package org.wa9nnn.wfdserver.scoring

import java.time.Instant
import java.time.temporal.ChronoUnit

import org.specs2.mutable.Specification
import org.wa9nnn.wfdserver.model.{Exchange, Qso}

class MatchedQsoSpec extends Specification {
  implicit val timeMatcher = new TimeMatcher(java.time.Duration.ofHours(2))

  val band20m = "20M"
  val mode = "CW"
  val ourCs = "WA9NNN"
  val otherCs = "K2ORS"
  val ourStamp = Instant.now
  val otherStamp = ourStamp.plusSeconds(50)
  val ourExchange = Exchange(ourCs, "1O", "IL")
  val otherExchange = Exchange(otherCs, "1h", "NYC")
  val ourQso = Qso(band20m, mode, ourStamp, ourExchange, otherExchange)
  val otherQso = Qso(band20m, mode, otherStamp, otherExchange, ourExchange)

  "MatchedQso  CW QSO" should {

    "validate" in {
      val matchQso = MatchedQso(ourQso, Some(otherQso))
      matchQso.validate(ourCs)
      matchQso.score() must beEqualTo(QsoKind.cw)
    }

    "Too far apart in time" in {
      val matchQso = MatchedQso(ourQso, Some(otherQso.copy(ts = ourStamp.plus(3, ChronoUnit.HOURS))))
      matchQso.validate(ourCs)
      matchQso.score() must beEqualTo(QsoKind.timeDelta)
    }

  }
}
