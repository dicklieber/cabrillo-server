package com.wa9nnn.wfdserver.model

import java.time.{Instant, LocalDate, LocalTime}

import com.wa9nnn.wfdserver.model
import org.specs2.mutable.Specification

class PaperLogQsoSpec extends Specification {
  private val freq = "7.0125"
  private val in = PaperLogQso(freq,
    "CW",
    LocalDate.ofYearDay(1998, 25), LocalTime.MIDNIGHT,
    CallSign("W1AW"), "1I", "CT",
    CallSign("wa9nnn"))

  "PaperLogQso" >> {
    "CSV" >> {
      "round trip " >> {
        val csvLine = in.toCsvLine
        csvLine must beEqualTo(freq + ",CW,1998-01-25,00:00,W1AW,1I,CT,WA9NNN")
        val backAgain = PaperLogQso.fromCsv(csvLine, -1)

        backAgain must beEqualTo(in)
      }
      "round trip clears index " >> {
        val csvLine = in.copy(index = 42).toCsvLine
        csvLine must beEqualTo(freq + ",CW,1998-01-25,00:00,W1AW,1I,CT,WA9NNN")
        val backAgain = PaperLogQso.fromCsv(csvLine, 2)

        backAgain.index must beEqualTo(2)
        backAgain.isCreate must beFalse
      }
    }
    "from callSign" >> {
      val cs = CallSign("WA9DEW")
      val qso = PaperLogQso(cs)
      qso.callSign must beEqualTo (cs)
      qso.isCreate must beTrue
    }
    "apply" >> {
      val maybeTuple: Option[(String, String, LocalDate, LocalTime, CallSign, String, String, CallSign, Int)] = PaperLogQso.unapply(in)
      val value1: (String, String, LocalDate, LocalTime, CallSign, String, String, CallSign, Int) = maybeTuple.get
      val backAgain = PaperLogQso(
        value1._1,
        value1._2,
        value1._3,
        value1._4,
        value1._5,
        value1._6,
        value1._7,
        value1._8
      )
      backAgain must beEqualTo (in)
    }

    "next" >> {
      val next = in.next
      next.isCreate must beTrue
      next.theirCall must beEqualTo(CallSign.empty)
      next.freq must beEqualTo(freq)
      next.section must beEmpty
      next.category must beEmpty
    }
    "withIndex" >> {
      "happy" >> {
        in.withIndex(123).index must beEqualTo(123)
      }
      "already has index" >> {
        val withIndex = in.withIndex(123)
        withIndex.index must beEqualTo(123)
        withIndex.withIndex(999) must throwAn[IllegalStateException]
      }
    }
    "Row" >> {
      val rowE = in.toRow(true)
      rowE.cssClass  must contain("editing")
      val row = in.toRow()
      row.cssClass  must not contain("editing")
    }
  }
}
