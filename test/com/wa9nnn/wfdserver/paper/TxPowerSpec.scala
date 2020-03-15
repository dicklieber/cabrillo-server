package com.wa9nnn.wfdserver.paper

import org.specs2.mutable.Specification

class TxPowerSpec extends Specification {
  "TxPower" >> {
    TxPower.low.toString must beEqualTo("low")
    val q = TxPower.qrp
    q.toString must beEqualTo("qrp")
  }
}
