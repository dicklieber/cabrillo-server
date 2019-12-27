
package controllers

import be.objectify.deadbolt.scala.ActionBuilders
import javax.inject.Inject
import org.wa9nnn.wfdserver.auth.WfdSubject._
import org.wa9nnn.wfdserver.auth.{Credentials, UserPassword}
import org.wa9nnn.wfdserver.db.DBRouter
import org.wa9nnn.wfdserver.util.JsonLogging
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

class LoginController @Inject()(cc: ControllerComponents,
                                db: DBRouter,
                                credentials: Credentials,
                                actionBuilder: ActionBuilders) extends AbstractController(cc) with JsonLogging {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  private val loginForm = Form(
    mapping(
      "userId" -> nonEmptyText,
      "password" -> nonEmptyText
    )(UserPassword.apply)(UserPassword.unapply)
  )

  def login: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.login()).withSession()
  }

  def dologin: Action[AnyContent] = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        BadRequest(views.html.login())
      },
      userPassword => {
        /* binding success, you get the actual value. */
        credentials.validate(userPassword) match {
          case Some(subject) =>
            logJson("Login")
              .++("userId" -> subject.identifier)
              .++("roles" -> subject.roles.map(_.toString).mkString(","))
              .++("remoteAddress" -> request.remoteAddress)
              .info()

            val result: Result = Redirect(routes.AdminController.adminlanding())
            result.withSession(sessionKey -> subject.toJson)
          case None =>
            logJson("Not Authenticated")
              .++("userId" -> userPassword.userId)
              .++("remoteAddress" -> request.remoteAddress)
              .error()
            Redirect(routes.LoginController.login()).flashing("errorMessage" -> "Unknown user or bad password!")
        }
      }
    )
  }
}

