package org.wa9nnn.wfdserver.auth

import java.nio.file.{Files, Path}
import java.util

import com.typesafe.config.ConfigFactory
import org.specs2.mutable.Specification

import scala.jdk.CollectionConverters._

class CredentialsDaoSpec extends Specification {

  def newDao(n:Int): CredentialsDao = {
    val dir: Path = Files.createTempDirectory("CredentialsDaoSpec")
    val path: Path = dir.resolveSibling(s"credFie$n")
    Files.deleteIfExists(path)
    val map: util.Map[String, String] = Seq("wfd.credentialsFile" -> path.toString).toMap.asJava
    new CredentialsDao(ConfigFactory.parseMap(map))
  }

  "CredentialsDao" should {
    "list" in {
      val dao = newDao(1)
      //      Files.del
      val list = dao.list
      list must haveLength(1)
    }
    "add new" >> {
      val dao = newDao(2)
      //      Files.delete(path)
      val list = dao.list
      val b4Length = list.length
      dao.upsert(UserPasswordRoles("fred", "sf", data = true))
      dao.list must haveLength(b4Length + 1)

    }

    "change password and roles" in {
      val dao = newDao(3)
      val list = dao.list
      val b4Length = list.length
      val head: UserPasswordRoles = list.head
      val userId = head.userId
      dao.validate(UserPassword(userId, "swordfish")) must beSome[WfdSubject]
      val newPassword = "fisb"

      dao.upsert(head.copy(password = newPassword, data = false))
      list must haveLength(b4Length)
      val backAgain = dao.getForEdit(userId).get
      backAgain.userId must beEqualTo(userId)
      dao.validate(UserPassword(userId, "swordfish")) must beNone
      dao.validate(UserPassword(userId, newPassword)) must beSome[WfdSubject]
    }

  }
}
