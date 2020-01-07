package org.wa9nnn.wfdserver.scoring

import java.time.Instant

import org.specs2.mutable.Specification
import org.wa9nnn.wfdserver.db.{Exchange, Qso}

class QsoAccumulatorSpec extends Specification {
  val exchange = Exchange("WA9NNN", "2I IL")

  "QsoAccumulatorSpec" >> {
    "happy" in {
      val QsoAccumulator = new QsoAccumulator
      val qso = Qso("7100", "CW", Instant.EPOCH, exchange, exchange)
      QsoAccumulator(qso)
      val result = QsoAccumulator.result(1)
      result.qsoPoints must beEqualTo (2)
      result.bandModeMultiplier must beEqualTo (1)
    }
    "one band two modes" in {
      val QsoAccumulator = new QsoAccumulator
      QsoAccumulator(Qso("7100", "CW", Instant.EPOCH, exchange, exchange))
      QsoAccumulator(Qso("7100", "DI", Instant.EPOCH, exchange, exchange))
      val result = QsoAccumulator.result(1)
      result.qsoPoints must beEqualTo (4)
      result.bandModeMultiplier must beEqualTo (2)
      result.byMode must haveLength(2)
      result.byBand must haveLength(1)
    }
    "two band three modes" in {
      val QsoAccumulator = new QsoAccumulator
      QsoAccumulator(Qso("7100", "CW", Instant.EPOCH, exchange, exchange))
      QsoAccumulator(Qso("7100", "DI", Instant.EPOCH, exchange, exchange))
      QsoAccumulator(Qso("20M", "PH", Instant.EPOCH, exchange, exchange))
      val result = QsoAccumulator.result(1)
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
