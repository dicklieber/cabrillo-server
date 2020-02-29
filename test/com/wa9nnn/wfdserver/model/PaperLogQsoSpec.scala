package com.wa9nnn.wfdserver.model

import java.time.Instant

import org.specs2.mutable.Specification

class PaperLogQsoSpec extends Specification {

  "PaperLogQso" >> {
    "CSV" >> {
      val in = PaperLogQso("CW", Instant.EPOCH, "W1AW", "1I CT")
      val csvLine = in.toCsvLine
      csvLine must beEqualTo ("CW,1970-01-01T00:00:00Z,W1AW,1I CT")
      val backAgain = PaperLogQso.apply(mode = csvLine)

      backAgain must beEqualTo (in)
    }

  }
}
