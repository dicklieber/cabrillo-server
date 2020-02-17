package com.wa9nnn.wfdserver.auth

import org.specs2.mutable.Specification

class UsersSpec extends Specification {

  "Users" should {
    "empty" >> {
      val users = Users()
      users.list must haveLength(1)
    }

    "upsert new" >> {
      val users = Users()
      val b4Length = users.size
      val newUserId = "fred"
      val newUserPassword = "sf"
      val changed: Users = users.upsert(UserPasswordRoles(newUserId, newUserPassword, data = true))

      changed.size must beEqualTo(2)
      changed.validate(UserPassword(newUserId, newUserPassword)) must_!= beSome[WfdSubject]

    }
    "validate" in {
      ok
    }

    "getForEdit" in {
      ok
    }

    "size" in {
      ok
    }

    "users" in {
      ok
    }

    "delete no admin" in {
      val users = Users()
      users.delete("dick") must throwAn[IllegalStateException]
    }
    "list" in {
      ok
    }

  }
}
