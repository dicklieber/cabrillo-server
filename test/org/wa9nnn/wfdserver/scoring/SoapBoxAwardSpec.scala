package org.wa9nnn.wfdserver.scoring

import org.specs2.mutable.Specification

class SoapBoxAwardSpec extends Specification {

  "SoapBoxAward" >> {
    "default claimed" >> {
      SoapBoxAward("any", "body").claimedEqAwarded must beTrue
    }
    "different claimed" >> {
      SoapBoxAward("any", "body", None, 200000).claimedEqAwarded must beFalse
    }

  }
}
