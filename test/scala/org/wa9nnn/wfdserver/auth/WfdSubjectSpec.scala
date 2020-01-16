package org.wa9nnn.wfdserver.auth

import org.specs2.mutable.Specification

class WfdSubjectSpec extends Specification {
  "WfdSubject" >> {
    "round Trip JSON" >> {
      val wfdSubject = WfdSubject("dick")
      val json = wfdSubject.toJson
      json must beEqualTo("""{"identifier":"dick","roles":[{"name":"UserManager"}],"dbName":""}""")
      val backAgain = WfdSubject.fromJson(json)
      backAgain must beEqualTo(wfdSubject)
    }
    "permission" >> {
      // not using permissions but test to keep coverage score higher
      val wfdSubject = WfdSubject("dick")
      wfdSubject.permissions must beEmpty
    }

  }
}
