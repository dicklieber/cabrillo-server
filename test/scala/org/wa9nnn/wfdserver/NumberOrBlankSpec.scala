package com.wa9nnn.wfdserver

import org.specs2.mutable.Specification

class NumberOrBlankSpec extends Specification {

  "NumberOrBlank" should {
    "apply" in {
      NumberOrBlank(0) must beEqualTo("&nbsp;")
      NumberOrBlank(1) must beEqualTo("1")
      NumberOrBlank(1000) must beEqualTo("1,000")
    }

  }
}
