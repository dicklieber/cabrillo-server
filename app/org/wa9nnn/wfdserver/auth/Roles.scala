
package org.wa9nnn.wfdserver.auth

import be.objectify.deadbolt.scala.models.Role

case class WfdRole(name: String) extends Role

object Roles {
  val adminRole: WfdRole = WfdRole("Admin")
  val userRole: WfdRole = WfdRole("User")

}

