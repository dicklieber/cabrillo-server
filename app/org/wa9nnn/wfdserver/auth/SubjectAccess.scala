
package org.wa9nnn.wfdserver.auth

import be.objectify.deadbolt.scala.AuthenticatedRequest
import play.api.mvc.{AnyContent, Request}

trait SubjectAccess {
  implicit def subject(implicit request: Request[AnyContent]): WfdSubject = {
    val subject1 = request.asInstanceOf[AuthenticatedRequest[AnyContent]].subject
    subject1.get.asInstanceOf[WfdSubject]
  }
}
