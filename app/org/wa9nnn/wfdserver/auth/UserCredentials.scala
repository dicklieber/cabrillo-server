
package org.wa9nnn.wfdserver.auth
import org.wa9nnn.wfdserver.auth.Roles._


case class UserCredentials(userId: String, bcrypt: String, roles: List[WfdRole]) {
  def isAdmin: Boolean = roles.contains(USERMANAGER_ROLE)
}

import com.github.t3hnar.bcrypt._

import UserPassword._

/**
 * A user and it's password.
 * This is the only place that deals with bcrypt.
 *
 * see https://en.wikipedia.org/wiki/Bcrypt
 * We use https://github.com/t3hnar/scala-bcrypt
 *
 * @param userId       user
 * @param rawPassword  plain text.
 */
case class UserPassword(userId: String, rawPassword: String) {

  def bcrypt: String = doBrycpt(rawPassword)

  def passwordOk(bcrypt: String): Boolean = {
    rawPassword.isBcrypted(bcrypt)
  }

}

object UserPassword {
  def doBrycpt(rawPassword: String): String = rawPassword.bcrypt
}
