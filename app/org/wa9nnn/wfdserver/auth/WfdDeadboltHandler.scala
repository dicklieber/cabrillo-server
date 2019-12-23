
package org.wa9nnn.wfdserver.auth

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.{AuthenticatedRequest, DeadboltHandler, DynamicResourceHandler}
import controllers.routes
import org.wa9nnn.wfdserver.auth.WfdSubject._
import play.api.mvc.{AnyContent, Request, Result, Results}
import views.html.{denied, login}
import play.api.mvc.Results.Redirect

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WfdDeadboltHandler extends DeadboltHandler {

  override def beforeAuthCheck[A](request: Request[A]): Future[Option[Result]] = Future {
    None
  }

  override def getDynamicResourceHandler[A](request: Request[A]): Future[Option[DynamicResourceHandler]] = Future {
    None
  }

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] =
    Future {
      request.subject.orElse {
        // replace request.session.get("userId") with how you identify the user
        request.session.get(sessionKey) match {
          case Some(jsonWfdSubject) =>
            Some(WfdSubject.fromJson(jsonWfdSubject)) //todo use Credential class
          // get from database, identity platform, cache, etc, if some
          // identifier is present in the request
          case _ => None
        }
      }
    }

  override def onAuthFailure[A](request: AuthenticatedRequest[A]): Future[Result] = {
    Future(Redirect(routes.LoginController.login()))
    //    getSubject(request).map(maybeSubject => maybeSubject.map(subject => (true, denied(Some(subject))))
    //      .getOrElse {
    //        (false, login()(request))
    //      })
    //      .map(subjectPresentAndContent =>
    //        if (subjectPresentAndContent._1) Results.Forbidden(subjectPresentAndContent._2)
    //        else Results.Unauthorized(subjectPresentAndContent._2))
  }
}
