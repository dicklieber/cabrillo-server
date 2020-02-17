package com.wa9nnn.wfdserver.util

import org.specs2.mutable.Specification
import com.wa9nnn.wfdserver.htmlTable.TextRenderer

class CountedSpec extends Specification {
  val counted = new Counted[String]
  counted("Dick")
  counted("Andy")
  counted("Andy")

  "Counted" should {
    "happy" in {
      val result: CountedThings[String] = counted.result
      result.map("Dick") must beEqualTo(1)
      result.map("Andy") must beEqualTo(2)
      ok
    }

    "round trip" in {
      val result: CountedThings[String] = counted.result
      val cn2 = new Counted[String]
      cn2("April")
      cn2("Dick")
      cn2(result)

      val cn2Map = cn2.result.map
      cn2Map("Dick") must beEqualTo(2)
      cn2Map("Andy") must beEqualTo(2)
      cn2Map("April") must beEqualTo(1)
      cn2Map must haveSize(3)

    }

    "rows" in {
      TextRenderer(counted.result.rows) must beEqualTo ("""||Dick|1|
                                                           ||Andy|2|""".stripMargin)
    }

  }
}
