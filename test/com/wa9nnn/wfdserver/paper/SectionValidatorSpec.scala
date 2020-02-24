package com.wa9nnn.wfdserver.paper

import org.specs2.mutable.Specification
import play.api.data.validation.{Invalid, Valid}

class SectionValidatorSpec extends Specification {
  val secValidator = new SectionValidator()

  "SectionValidator" should {
    "OK" in {
      secValidator.passwordCheckConstraint("IL") must beEqualTo(Valid)
    }
    "Unknown" in {
      secValidator.passwordCheckConstraint("3H") must beEqualTo(Invalid(("Unknown ARRL section: 3H")))
    }

  }
}
