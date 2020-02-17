package com.wa9nnn.wfdserver.auth

import org.specs2.mutable.Specification

class UserPasswordSpec extends Specification {

  "UserPassword" should {
    "bcrypt" in {
      val userPassword = UserPassword("me", "swordfish")
      val bcrypt = userPassword.bcrypt
      val bool = userPassword.passwordOk(bcrypt)
      bool must beTrue
    }

  }
}
