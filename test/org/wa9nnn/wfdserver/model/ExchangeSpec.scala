package org.wa9nnn.wfdserver.model

import org.specs2.mutable.Specification
import org.wa9nnn.cabrillo.parsers.Exchange_WFD
import org.wa9nnn.wfdserver.htmlTable.Cell

class ExchangeSpec extends Specification {
  val callSign = "WA9NNN"
  val category = "3I"
  val section = "IL"

  "Exchange" >> {
    "round trip category and section" >> {
      val exchange = Exchange(callSign, category, section)
      val (cat, sec) = exchange.categoryAndSection
      cat must beEqualTo (category)
      sec must beEqualTo (section)
    }

    "WFD he=lper" >> {
      val exchange = Exchange(Exchange_WFD(callSign, category, section))
      val (cat, sec) = exchange.categoryAndSection
      cat must beEqualTo (category)
      sec must beEqualTo (section)
    }

    "Table Cell" >> {
      val exchange = Exchange(Exchange_WFD(callSign, category, section))
      val cell: Cell = exchange.toCell
      cell.value must beEqualTo ("WA9NNN  3I IL") // note padding to 7 of callSign!
    }
  }
}
