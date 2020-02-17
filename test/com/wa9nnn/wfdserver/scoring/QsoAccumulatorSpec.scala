package com.wa9nnn.wfdserver.scoring

import java.time.{Duration, Instant}

import org.specs2.mutable.Specification
import com.wa9nnn.wfdserver.model.{Exchange, Qso}

class QsoAccumulatorSpec extends Specification {
  private val exchange = Exchange("WA9NNN", "2I IL")
 implicit val timeMatcher = new TimeMatcher(Duration.ofHours(2))
  "QsoAccumulatorSpec" >> {
    "happy" in {
      val qsoAccumulator = new QsoAccumulator
      val qso = Qso("40M", "CW", Instant.EPOCH, exchange, exchange)
      qsoAccumulator(qso)
      val result = qsoAccumulator.result
      result.qsoPoints must beEqualTo (2)
      result.bandModeMultiplier must beEqualTo (1)
    }
    "one band two modes" in {
      val qsoAccumulator = new QsoAccumulator
      qsoAccumulator(Qso("40M", "CW", Instant.EPOCH, exchange, exchange))
      qsoAccumulator(Qso("40M", "DI", Instant.EPOCH, exchange, exchange))
      val result = qsoAccumulator.result
      result.qsoPoints must beEqualTo (4)
      result.bandModeMultiplier must beEqualTo (2)
      result.byMode must haveLength(2)
      result.byBand must haveLength(1)
    }
    "two band three modes" in {
      val qsoAccumulator = new QsoAccumulator
      qsoAccumulator(Qso("40M", "CW", Instant.EPOCH, exchange, exchange))
      qsoAccumulator(Qso("40M", "DI", Instant.EPOCH, exchange, exchange))
      qsoAccumulator(Qso("20M", "PH", Instant.EPOCH, exchange, exchange))
      val result = qsoAccumulator.result
      result.qsoPoints must beEqualTo (5)
      result.bandModeMultiplier must beEqualTo (3)
      result.byMode must haveLength(3)
      result.byMode.find(_.mode == "CW").get.count must beEqualTo (1)
      result.byMode.find(_.mode == "DI").get.count must beEqualTo (1)
      result.byMode.find(_.mode == "PH").get.count must beEqualTo (1)
      result.byBand must haveLength(2)
      result.byBand.find(_.band == "40M").get.count must beEqualTo (2)
      result.byBand.find(_.band == "20M").get.count must beEqualTo (1)
    }

  }
}
