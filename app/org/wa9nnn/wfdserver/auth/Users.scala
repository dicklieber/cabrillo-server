
package org.wa9nnn.wfdserver.auth

import org.wa9nnn.wfdserver.auth.Roles._
import org.wa9nnn.wfdserver.auth.Users.defaultCrdentials
import play.api.libs.json.{Format, Json}

/**
 * This is immutable.
 *
 * @param users from file.
 */
case class Users(users: List[UserCredentials] = defaultCrdentials) {
  val size: Int = users.size


  private lazy val credentialsMap: Map[String, UserCredentials] = users.map(uc => uc.userId -> uc).toMap

  def validate(userPassword: UserPassword): Option[WfdSubject] = {
    for {
      uc <- credentialsMap.get(userPassword.userId)
      if userPassword.passwordOk(uc.bcrypt)
    } yield {
      val subject = WfdSubject(uc.userId, uc.roles) //todo we only have one role if we add more link string to Role
      subject
    }
  }

  def getForEdit(userId: String): Option[UserPasswordRoles] = {
    credentialsMap.get(userId).map {
      UserPasswordRoles(_)
    }
  }

  def list: List[UserPasswordRoles] = {
    credentialsMap.values.map(uc => UserPasswordRoles(uc))
      .toList
      .sorted
  }

  /**
   * Adds or replaces this users' entry.
   *
   * @param userPasswordRoles new or replacement
   * @return Some(User if updated) or None if no update.
   */
  def upsert(userPasswordRoles: UserPasswordRoles):  Users = {
    val maybeCurrentCredentials: Option[UserCredentials] = credentialsMap.get(userPasswordRoles.userId)
   val r: UserCredentials =  maybeCurrentCredentials match {
      case Some(value) =>  // has current
        userPasswordRoles.update(value)
      case None => // no current, must have
        assert(userPasswordRoles.password !=  UserPasswordRoles.keepPassword, "New user must have password")
        UserCredentials(userPasswordRoles.userId, UserPassword.doBrycpt(userPasswordRoles.password) , userPasswordRoles.toRoles)
    }

    doIfStillAdmin(credentialsMap.updated(r.userId, r).values.toList)
  }

  /**
   * This won't allow removing the last user with [[org.wa9nnn.wfdserver.auth.Roles#ADMIN_ROLE.]]
   *
   * @param userId to be removed.
   * @return Left Users if use was deleted and [[Users]] should be persisted and reloaded.
   */
  def delete(userId: String): Users = {
    doIfStillAdmin(credentialsMap.removed(userId).values.toList)
  }

  private def doIfStillAdmin(updated: List[UserCredentials]):  Users = {
    if (updated.exists(uc => uc.isAdmin))
      Users(updated)
    else {
      throw new IllegalStateException("This would delete the last user wih Admin privilege!")
    }

  }
}

object Users {
  val defaultCrdentials = List(
    UserCredentials(userId = "dick",
      bcrypt = "$2a$10$SISHId.lTxwDtUN45BKhKOcU/FnVy9Fl57v6W0Zrt2VYaQ2zIVqmO", // swordfish
      List(USERMANAGER_ROLE, DATA_ROLE)
    ))

  implicit val roleFormat: Format[WfdRole] = Json.format[WfdRole]
  implicit val subjectFormat: Format[WfdSubject] = Json.format[WfdSubject]
  implicit val upFormat: Format[UserPassword] = Json.format[UserPassword]
  implicit val credentialsFormat: Format[UserCredentials] = Json.format[UserCredentials]
  implicit val usersFormat: Format[Users] = Json.format[Users]
}
