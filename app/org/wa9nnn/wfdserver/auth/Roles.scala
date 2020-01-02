
package org.wa9nnn.wfdserver.auth

import be.objectify.deadbolt.scala.models.Role

case class WfdRole(name: String) extends Role

object Roles {
  val USERMANAGER_ROLE: WfdRole = WfdRole("UserManager")
  val DATA_ROLE: WfdRole = WfdRole("Data")

}

