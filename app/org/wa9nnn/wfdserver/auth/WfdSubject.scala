
package org.wa9nnn.wfdserver.auth

import be.objectify.deadbolt.scala.models.{Permission, Subject}
import play.api.libs.json.{Format, Json}

/**
 * Represents a an admin (full privilege) or user (can view stuff but not change user credentials).
 * Not used for those uploading log files.
 * This lives in the play session as json when the user is logged in.
 *
 * @param identifier name of the user
 * @param roles      what user can do
 */
case class WfdSubject(identifier: String, roles: List[WfdRole] = List(Roles.USERMANAGER_ROLE)) extends Subject {

  override def permissions: List[Permission] = List.empty

  import WfdSubject.wfdSubjectFormat

  def toJson: String = Json.toJson(this).toString()
}

object WfdSubject {
  implicit val wfdRoleFormat: Format[WfdRole] = Json.format[WfdRole]
  implicit val wfdSubjectFormat: Format[WfdSubject] = Json.format[WfdSubject]

  def fromJson(json: String): WfdSubject = {
    Json.parse(json).as[WfdSubject]
  }

  val sessionKey = "subject"
}
