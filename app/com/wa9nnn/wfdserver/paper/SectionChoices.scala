
package com.wa9nnn.wfdserver.paper

import com.wa9nnn.cabrillo.contests.ArrlSections
import javax.inject.Singleton

@Singleton
class SectionChoices {
 val autoComplete: String =  new ArrlSections()
    .byCode
    .keys.toSeq
    .sorted
   .map(s => s""""$s"""")
   .mkString(",")

  println(autoComplete)
}
