
package controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.typesafe.config.Config
import javax.inject.Inject
import com.wa9nnn.wfdserver.auth.WfdSubject._
import com.wa9nnn.wfdserver.auth.{CredentialsDao, UserPassword}
import com.wa9nnn.wfdserver.db.DBRouter
import com.wa9nnn.wfdserver.recaptcha.RecaptchaSupport
import com.wa9nnn.wfdserver.util.JsonLogging
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

class LoginController @Inject()(implicit cc: ControllerComponents,
                                db: DBRouter,
                                dao: CredentialsDao,
                                recaptchaSupport: RecaptchaSupport,
                                config: Config,
                                actionBuilder: ActionBuilders) extends AbstractController(cc) with JsonLogging {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  private val loginForm = Form(
    mapping(
      "userId" -> nonEmptyText,
      "password" -> nonEmptyText,
      "captchatoken" -> text
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

        val captchaOk = recaptchaSupport(userPassword.captchatoken)
        if (captchaOk) {
          dao.validate(userPassword) match {
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
        }else{
          Ok("scored poorly")
        }
      }
    )
  }
}

