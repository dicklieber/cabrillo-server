
package controllers


import be.objectify.deadbolt.scala.ActionBuilders
import javax.inject._
import org.wa9nnn.wfdserver.auth.SubjectAccess
import org.wa9nnn.wfdserver.db.DBRouter
import org.wa9nnn.wfdserver.htmlTable._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, _}

import scala.concurrent.Future

/**
 * Administrative pages
 */
@Singleton
class SearchController @Inject()(cc: ControllerComponents,
                                 db: DBRouter,
                                 actionBuilder: ActionBuilders
                                )
  extends AbstractController(cc)
    with play.api.i18n.I18nSupport
    with SubjectAccess{
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global


  def index(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request: Request[AnyContent] =>
      Future {
        val table = MultiColumn(Seq.empty, 10, s"Submissions (0)").withCssClass("resultTable")
        Ok(views.html.search(table, searchForm))
      }
  }

  def search(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request: Request[AnyContent] =>

      searchForm.bindFromRequest.fold(
        formWithErrors => {
          // binding failure, you retrieve the form containing errors:
         Future( BadRequest(views.html.search(tableEmpty, formWithErrors)))
        },
        userData => {
          /* binding success, you get the actual value. */
          val partialCallSign = userData.partialCallSign
          //          Ok(views.html.search(tableEmpty, userData, dbName))
          db.search(partialCallSign).map { callSignIds =>
            val table = MultiColumn(callSignIds.map(_.toCell), 10, s"Submissions (${callSignIds.length})").withCssClass("resultTable")
            val form = searchForm.fill(userData)
            Ok(views.html.search(table, form))
          }
        }
      )
  }

  private val searchForm: Form[SearchCriterion] = Form(
    mapping(
      "partialCallSign" -> text,
    )(SearchCriterion.apply)(SearchCriterion.unapply)
  )

  private val tableEmpty = Table(Header("None Found"), Seq.empty)
}

case class SearchCriterion(partialCallSign: String)

