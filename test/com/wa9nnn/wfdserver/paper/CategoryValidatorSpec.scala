package com.wa9nnn.wfdserver.paper

import org.specs2.matcher.DataTables
import org.specs2.mutable.Specification
import play.api.data.validation.{Invalid, Valid, ValidationResult}

class CategoryValidatorSpec extends Specification with DataTables {

  val cv = new CategoryValidator().categoryConstraint
  val expectInvalid = Invalid("Number followed by I, O or H")
  "CategoryValidator" >> {
    "Category" | "expected" |
      "ih" !! expectInvalid |
      "2X" !! expectInvalid |
      "99H" !! Valid |
      "1H" !! Valid |
      "1I" !! Valid |
      "1O" !! Valid |> { (candidate: String, expected: ValidationResult) =>
      cv(candidate) must beEqualTo(expected)
    }
  }
}
