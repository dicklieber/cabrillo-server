package com.wa9nnn.wfdserver.scoring

import java.time.temporal.ChronoUnit
import java.time.{Duration, Instant}

import org.specs2.mutable.Specification

class TimeMatcherSpec extends Specification {
  val timeMatcher = new TimeMatcher(Duration.ofHours(2))
  "TimeMatcher" >> {
    "closeEnough" >> {
      "exact" >> {
        val instant = Instant.now
        timeMatcher(instant, instant) must beTrue
      }
      "close enough" >> {
        val instant = Instant.now
        timeMatcher(instant, instant.plus(1, ChronoUnit.HOURS)) must beTrue
      }
      "too far away" >> {
        val instant = Instant.now
        timeMatcher(instant, instant.plus(3, ChronoUnit.HOURS)) must beFalse
      }
    }

  }
}
