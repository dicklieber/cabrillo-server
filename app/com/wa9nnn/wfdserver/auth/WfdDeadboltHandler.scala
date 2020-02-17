
package com.wa9nnn.wfdserver.auth

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.{AuthenticatedRequest, DeadboltHandler, DynamicResourceHandler}
import controllers.routes
import com.wa9nnn.wfdserver.auth.WfdSubject._
import play.api.mvc.Results.Redirect
import play.api.mvc.{Request, Result}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.Exception.allCatch

/**
 * Defines the interface between the DeadBolt authorization framework and this server.
 * The Login controller places a [[WfdSubject]] into the Play session getSubject, below, retrieves it
 */
class WfdDeadboltHandler extends DeadboltHandler {


  /**
   * Gets the current subject e.g. the current user.
   *
   * @return a future for an option containing the current subject
   */
  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] =
    Future {
      request.subject.orElse {
        // replace request.session.get("userId") with how you identify the user
        request.session.get(sessionKey) match {
          case Some(jsonWfdSubject) =>
            allCatch opt WfdSubject.fromJson(jsonWfdSubject)
          case _ => None
        }
      }
    }

  override def onAuthFailure[A](request: AuthenticatedRequest[A]): Future[Result] = {
    Future(Redirect(routes.LoginController.login()))
  }
  override def beforeAuthCheck[A](request: Request[A]): Future[Option[Result]] = Future {
    None
  }

  override def getDynamicResourceHandler[A](request: Request[A]): Future[Option[DynamicResourceHandler]] = Future {
    None
  }

}
