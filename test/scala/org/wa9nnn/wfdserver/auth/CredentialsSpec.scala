package com.wa9nnn.wfdserver.auth

import java.nio.file.{Files, Path}

import org.specs2.mutable.Specification

//class CredentialsSpec extends Specification {
//  sequential
//  private val tempFile: Path = Files.createTempFile("credentials", ".jsons")
//  private val credentials = new CredentialsDao(tempFile)
//  private val userId = "dick"
//  val userPassword = UserPassword(userId, "swordfish")
//  val userPassword2 = UserPassword(userId, "swordfish2")
//
//  "Credentials" >> {
//    "initially be empty" >>{
//      credentials.list must haveLength(0)
//    }
//
////    "upsert new" >> {
////      credentials.upsert(userPassword)
////      credentials.list must haveLength(1)
////      val maybeSubject = credentials.validate(userPassword)
////      maybeSubject must beSome[WfdSubject]
////      maybeSubject.get.identifier must beEqualTo (userId)
////    }
////    "upsert update" >> {
////      credentials.upsert(userPassword2)
////      credentials.list must haveLength(1)
////      val maybeSubject = credentials.validate(userPassword2)
////      maybeSubject must beSome[WfdSubject]
////      maybeSubject.get.identifier must beEqualTo (userId)
////      credentials.validate(userPassword) must beNone
////    }
//
//    "delete" >> {
//      credentials.delete(userId)
//      credentials.list must beEmpty
//    }
//
//    "validate bad" >> {
//      credentials.validate(UserPassword("elwood", "dowd")) must beNone
//    }
//
//
//
//  }
//}
