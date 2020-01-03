package org.wa9nnn.wfdserver.util

import java.time.Instant

import org.specs2.mutable.Specification

class TimeConvertersSpecs extends Specification {

  "TimeConverters" should {
    "instantDisplay" in {
      val str = TimeConverters.instantDisplayUTCCST(Instant.EPOCH)
      str must beEqualTo ("""01/01/70 00:00 UTC (12/31/69 18:00 CST)""")
    }

  }
}
