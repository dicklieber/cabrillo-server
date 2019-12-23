
package org.wa9nnn.wfdserver.auth

import java.io.PrintWriter
import java.nio.file.{Files, Path, Paths}

import be.objectify.deadbolt.scala.models.Role
import javax.inject.Inject
import org.wa9nnn.wfdserver.auth.Credentials._
import org.wa9nnn.wfdserver.util.JsonLogging
import play.api.libs.json.{Format, Json}
import play.api.mvc.{AnyContent, Request}

import scala.io.Source
import scala.util.{Try, Using}

class Credentials(path: Path = Paths.get("var/wfd/credentials")) extends JsonLogging {
  logger.info(s"path: ${path.toString}  absolute:${path.toAbsolutePath.toString}")

  private var credentialsMap: Map[String, UserCredentials] = Map.empty

  readFile()

  /**
   *
   * @param userPassword to test
   * @return Some[WfdSubject] if user exists and password matches None if user does not exist or password doesn't match.
   */
  def validate(userPassword: UserPassword): Option[WfdSubject] = {
    for {
      c <- credentialsMap.get(userPassword.userId)
      if userPassword.passwordOk(c.bcrypt)
    } yield {
      val subject = WfdSubject(c.userId, List(Roles.adminRole)) //todo we only have one role if we add more link string to Role
      subject
    }
  }

  /**
   * Adds or replaces this users' entry.
   *
   * @param userPassword new or replacement
   */
  def upsert(userPassword: UserPassword): Unit = {
    val userCredentials = UserCredentials(userPassword)
    val n = credentialsMap.updated(userCredentials.userId, userCredentials)
    credentialsMap = n
    save()
  }

  def save(): Unit = {
    Using(new PrintWriter((Files.newBufferedWriter(path)))) { writer =>
      credentialsMap.values.foreach { uc =>
        writer.println(Json.toJson(uc).toString())
      }
      logger.debug(s"saved ${credentialsMap.size} to ${path.toString}")
    }
  }

  def delete(userId: String): Unit = {
    credentialsMap = credentialsMap - userId
    save()
  }

  def readFile(): Unit = {
    val t: Try[Unit] = Using(Source.fromFile(path.toFile)) { br =>
      credentialsMap = br.getLines().map { line: String =>
        val uc = Json.parse(line).as[UserCredentials]
        uc.userId -> uc
      }.toMap
      logger.info(s"loaded ${credentialsMap.size} credential(s) from ${path.toString}")
    }

    t.failed.foreach { t =>
      logJson("Loading credentials").++("path" -> path).error(t)
    }
  }

  def list: Seq[UserRoles] = {
    credentialsMap.values.map(_.toUserRoles).toSeq.sorted
  }
}

object Credentials {
  implicit val upFormat: Format[UserPassword] = Json.format[UserPassword]
  implicit val credentialsFormat: Format[UserCredentials] = Json.format[UserCredentials]
}

case class UserCredentials(userId: String, bcrypt: String, roles: Seq[String]) {
  def toUserRoles: UserRoles = UserRoles(userId, roles)
}

object UserCredentials {
  def apply(userPassword: UserPassword, roles: Seq[Role] = Seq(Roles.adminRole)): UserCredentials = {

    new UserCredentials(userPassword.userId, userPassword.bcrypt, roles.map(_.name))
  }
}

case class UserRoles(userId: String, roles: Seq[String]) extends Ordered[UserRoles] {
  override def compare(that: UserRoles): Int = userId.compareToIgnoreCase(that.userId)
}

/**
 * @param userId       user
 * @param rawPassword  plain text.
 */
case class UserPassword(userId: String, rawPassword: String) {

  import com.github.t3hnar.bcrypt._

  def bcrypt: String = rawPassword.bcrypt

  def passwordOk(bcrypt: String) = {
    rawPassword.isBcrypted(bcrypt)
  }

}