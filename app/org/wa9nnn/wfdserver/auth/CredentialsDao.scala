
package org.wa9nnn.wfdserver.auth

import java.nio.file.{Files, Paths}

import com.typesafe.config.Config
import javax.inject.{Inject, Singleton}
import org.wa9nnn.wfdserver.util.{JsonLogging, SaveWithBackup}
import play.api.libs.json.Json

import scala.util.Using

/**
 * Handles credentials persistence.
 */
@Singleton
class CredentialsDao @Inject()(config: Config) extends JsonLogging {

  private val credentialsFilePath = Paths.get(config.getString("wfd.credentialsFile"))

  private var users = Users()

  readFile()

  /**
   *
   * @param userPassword to test
   * @return Some[WfdSubject] if user exists and password matches None if user does not exist or password doesn't match.
   */
  def validate(userPassword: UserPassword): Option[WfdSubject] = {
    users.validate(userPassword)
  }
  def getForEdit(userId: String): Option[UserPasswordRoles] = {
    users.getForEdit(userId)
  }


  /**
   * Adds or replaces this users' entry.
   *
   * @param userPasswordRoles new or replacement
   */
  def upsert(userPasswordRoles: UserPasswordRoles): Unit = {

    val value = users.upsert(userPasswordRoles)
    saveAndRead(value)
  }

  /**
   * Opposite of [[upsert()]].
   * @param userId to be removed.
   */
  def delete(userId: String): Unit = {
    saveAndRead(users.delete(userId))
  }

  def saveAndRead(newusers :Users): Unit = {
   val sJson = Json.prettyPrint( Json.toJson(newusers))
    SaveWithBackup(credentialsFilePath, sJson)
    readFile()
  }

  def readFile(): Unit = {
    if(Files.exists(credentialsFilePath)) {
      Using(Files.newInputStream(credentialsFilePath)) { inputStream =>
        users = Json.parse(inputStream).as[Users]
        logger.info(s"Loaded ${users.size} from $credentialsFilePath")
      }
    }else{
      logger.error(s"Did not find credentials file at $credentialsFilePath, use defalt user to at lease one admin user.")
    }
  }

  def list: Seq[UserPasswordRoles] = {
    users.list
  }
}

