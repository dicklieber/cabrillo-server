
package com.wa9nnn.wfdserver.auth

import com.wa9nnn.wfdserver.auth.Roles._
import com.wa9nnn.wfdserver.auth.UserPasswordRoles._
import com.wa9nnn.wfdserver.util.JsonLogging
import com.wa9nnn.wfdserver.auth.UserPassword.doBrycpt

/**
 * This is data transfer object between the server and the user editor HTML page.
 *
 * @param userId      user name
 * @param password    when user from server to HTML page this is [[keepPassword]]. If the user changes it then it will be something else.
 * @param data        can access admin pages    what the user is authorized to do.
 * @param userManager can access user manager    what the user is authorized to do.
 * @param cmd         used to hold submit button value.
 */
case class UserPasswordRoles(userId: String = "", password: String = keepPassword, data: Boolean = true, userManager: Boolean = false, cmd: String = "") extends Ordered[UserPasswordRoles] with JsonLogging {

  def toRoles: List[WfdRole] = {
    var roles = List.empty[WfdRole]
    if (data) {
      roles = roles :+ Roles.DATA_ROLE
    }
    if (userManager) {
      roles = roles :+ Roles.USERMANAGER_ROLE
    }
    roles
  }

  def rolesString: String = {
    toRoles.map(_.name).mkString(", ")
  }

  /**
   * Replace current credential with new data.
   *
   * @param currentCredentials in th system
   * @return  updated  UserCredentials
   */
  def update(currentCredentials: UserCredentials): UserCredentials = {

    val encryptedPassword = if (password == keepPassword) {
      currentCredentials.bcrypt
    } else {
      doBrycpt(password)
    }
    UserCredentials(userId, encryptedPassword, toRoles)
  }

  def maybeChangedPassword: Option[String] = if (password == keepPassword)
    None
  else
    Some(password)

  override def compare(that: UserPasswordRoles): Int = userId.compareToIgnoreCase(that.userId)
}

object UserPasswordRoles {
  def apply(userCredentials: UserCredentials): UserPasswordRoles = {
    new UserPasswordRoles(userId = userCredentials.userId,
      password = keepPassword,
      data = userCredentials.roles.contains(DATA_ROLE),
      userManager = userCredentials.roles.contains(USERMANAGER_ROLE))
  }

  val keepPassword = "$$Keep-Current-Unchanged++"
}

