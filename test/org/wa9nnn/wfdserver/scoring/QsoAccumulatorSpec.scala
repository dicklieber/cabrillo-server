package org.wa9nnn.wfdserver.scoring

import java.time.Instant

import org.specs2.mutable.Specification
import org.wa9nnn.cabrillo.parsers.ExchangeNoParse
import org.wa9nnn.wfdserver.db.mongodb.QSO

class QsoAccumulatorSpec extends Specification {

  "QsoAccumulatorSpec" >> {
    "happy" in {
      val qsoAccumulator = new QsoAccumulator
      val qso = QSO("7100", "CW", Instant.EPOCH, ExchangeNoParse, ExchangeNoParse)
      qsoAccumulator(qso)
      val result = qsoAccumulator.result(1)
      result.qsoPoints must beEqualTo (2)
      result.bandModeMultiplier must beEqualTo (1)
    }
    "one band two modes" in {
      val qsoAccumulator = new QsoAccumulator
      qsoAccumulator(QSO("7100", "CW", Instant.EPOCH, ExchangeNoParse, ExchangeNoParse))
      qsoAccumulator(QSO("7100", "DI", Instant.EPOCH, ExchangeNoParse, ExchangeNoParse))
      val result = qsoAccumulator.result(1)
      result.qsoPoints must beEqualTo (2)
      result.bandModeMultiplier must beEqualTo (2)
      result.byMode must haveLength(2)
      result.byBand must haveLength(1)
    }
    "two band three modes" in {
      val qsoAccumulator = new QsoAccumulator
      qsoAccumulator(QSO("7100", "CW", Instant.EPOCH, ExchangeNoParse, ExchangeNoParse))
      qsoAccumulator(QSO("7100", "DI", Instant.EPOCH, ExchangeNoParse, ExchangeNoParse))
      qsoAccumulator(QSO("20M", "PH", Instant.EPOCH, ExchangeNoParse, ExchangeNoParse))
      val result = qsoAccumulator.result(1)
      result.qsoPoints must beEqualTo (2)
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
