
package com.wa9nnn.wfdserver.paper

import com.wa9nnn.cabrillo.contests.{ContestInfoWFD, UnknownSectionCodeException}
import com.wa9nnn.cabrillo.requirements.ContestInfo
import javax.inject.{Inject, Singleton}
import play.api.data.validation.{Constraint, Invalid, Valid}

@Singleton
class SectionValidator @Inject()(contestInfo: ContestInfo = new ContestInfoWFD) {
  val sectionConstraint: Constraint[String] = Constraint("Unknown Section")({ sectionCandidate =>

    try {
      contestInfo.validateLocation(sectionCandidate)
      Valid
    } catch {
      case e: UnknownSectionCodeException =>
        Invalid(e.getMessage)
    }
  }
  )
}
