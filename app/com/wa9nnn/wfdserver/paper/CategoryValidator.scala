
package com.wa9nnn.wfdserver.paper

import com.wa9nnn.cabrillo.contests.{ContestInfoWFD, UnknownSectionCodeException}
import com.wa9nnn.cabrillo.requirements.ContestInfo
import javax.inject.{Inject, Singleton}
import play.api.data.validation.{Constraint, Invalid, Valid}

@Singleton
class CategoryValidator {
  private val r = """(\d+)([IHO])""".r
  val categoryConstraint: Constraint[String] = Constraint("Bad Category") { categoryCandidate =>

    if (r.matches(categoryCandidate))
      Valid
    else
      Invalid("Number followed by I, O or H")
  }
}
