
package controllers

import be.objectify.deadbolt.scala.ActionBuilders
import javax.inject.Inject
import org.wa9nnn.wfdserver.auth.{CredentialsDao, UserPasswordRoles}
import org.wa9nnn.wfdserver.htmlTable._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.Future

class UserController @Inject()(cc: ControllerComponents,
                               credentialsDao: CredentialsDao,
                               actionBuilder: ActionBuilders
                              )
  extends AbstractController(cc) with play.api.i18n.I18nSupport {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  def index(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      Future {
        val rows = credentialsDao.list.map { upr =>
          val link = routes.UserController.edit(upr.userId).url
          Row(
            Cell(upr.userId).withUrl(link),
            upr.rolesString
          )
        }

        val table = Table(Header("Users", "UserId", "Roles"), rows).withCssClass("resultTable")
        Ok(views.html.users(table))
      }
  }

  def update(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>

      Future(
        userForm.bindFromRequest.fold(
          formWithErrors => {
            // binding failure, you retrieve the form containing errors:
            BadRequest(views.html.userEditor(formWithErrors))
          },
          userData => {
            /* binding success, you get the actual value. */
            if (userData.cmd == "Delete") {
              credentialsDao.delete(userData.userId)
            } else {
              credentialsDao.upsert(userData)
            }
            Redirect(routes.UserController.index())
          }
        )
      )


  }

  def delete(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      Future(
        Ok("todo")
      )
  }

  def create(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      Future(
        Ok(views.html.userCreate(userForm))
      )
  }


  private val userForm: Form[UserPasswordRoles] = Form(
    mapping(
      "userId" -> nonEmptyText(minLength = 4),
      "password" -> text,
      "data" -> boolean,
      "userManager" -> boolean,
      "cmd" -> text
    )(UserPasswordRoles.apply)(UserPasswordRoles.unapply)
  )


  def edit(userId: String): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request =>
      Future {
        credentialsDao.getForEdit(userId).map { upr =>
          val filledInForm: Form[UserPasswordRoles] = userForm.fill(upr)
          Ok(views.html.userEditor(filledInForm))

        }.getOrElse(Ok(s"userId: $userId not found!"))
      }
  }
}
