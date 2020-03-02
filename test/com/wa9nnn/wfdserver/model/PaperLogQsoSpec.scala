package com.wa9nnn.wfdserver.model

import java.time.{Instant, LocalDate, LocalTime}

import org.specs2.mutable.Specification

class PaperLogQsoSpec extends Specification {

  "PaperLogQso" >> {
    "CSV " >> {
      val in = PaperLogQso("7.0125", "CW", LocalDate.ofYearDay(1998, 25), LocalTime.MIDNIGHT,
        CallSign("W1AW"), "1I", "CT",
        CallSign("wa9nnn"))
      val csvLine = in.toCsvLine
      csvLine must beEqualTo("7.0125,CW,1998-01-25,00:00,W1AW,1I,CT,WA9NNN")
      val backAgain = PaperLogQso.fromCsv(csvLine)

      backAgain must beEqualTo(in)
    }

  }
}
