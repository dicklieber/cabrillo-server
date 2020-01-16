
package controllers

import java.time.{LocalDateTime, ZoneId, ZoneOffset}

import be.objectify.deadbolt.scala.ActionBuilders
import com.google.inject.Inject
import javax.inject.Singleton
import org.wa9nnn.wfdserver.contest.{Message, SubmissionConfig, SubmissionControlDao, Times}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.Future

@Singleton
class SubmissionControlController @Inject()(cc: ControllerComponents, actionBuilder: ActionBuilders, submissionControlDao: SubmissionControlDao)
  extends AbstractController(cc)
    with play.api.i18n.I18nSupport {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  val messgeMapping = mapping(
    "h2" -> text,
    "message" -> text
  )(Message.apply)(Message.unapply)

  private val controlUiForm: Form[ControlUi] = Form(
    mapping(
      "beforeMessage" -> messgeMapping,
      "duringMessage" -> messgeMapping,
      "afterMessage" -> messgeMapping,
      "start" -> localDateTime("MM/dd/yyyy HH:mm"),
      "end" -> localDateTime("MM/dd/yyyy HH:mm")
    )(ControlUi.apply)(ControlUi.unapply)
  )


  def index(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request: Request[AnyContent] =>

      val sc = submissionControlDao.get
      val value = ControlUi(sc)
      val form = controlUiForm.fill(value)

      Future(Ok(views.html.submissionControl(form)))
  }


  def post(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() {
    implicit request: Request[AnyContent] =>

      Future(
        controlUiForm.bindFromRequest.fold(
          formWithErrors => {
            // binding failure, you retrieve the form containing errors:
            BadRequest(views.html.submissionControl(formWithErrors))
          },
          userData => {
            /* binding success, you get the actual value. */
            submissionControlDao.put(userData.toSubmissionConfig)
            Redirect(routes.SubmissionControlController.index())
          }
        )
      )

  }
}

case class ControlUi(beforeMessage: Message, duringMessage: Message, afterMessage: Message, start: LocalDateTime, end: LocalDateTime){
  def toSubmissionConfig:SubmissionConfig = {
    val times = Times(start.toInstant(ZoneOffset.UTC), end.toInstant(ZoneOffset.UTC))
    SubmissionConfig(beforeMessage, duringMessage,  afterMessage, times)
  }

//  val mdMessagesMap: Map[String, String] = sc.messagesMap.map{case(name, value) => name -> tomd.transform(value).getOrElse("???") }

//  val mdMessagesMap: Map[String, String] = messagesMap.map{case(name, value) => name -> tomd.transform(value).getOrElse("???") }
//  Future(Ok(views.html.submissionControl(form)))

}

object ControlUi {
  def apply(sc: SubmissionConfig): ControlUi = {
    val times = sc.times
    val start = LocalDateTime.ofInstant(times.submissionBegin, ZoneId.of("UTC"))
    val end = LocalDateTime.ofInstant(times.submissionEnd, ZoneId.of("UTC"))
    new ControlUi(sc.beforeMessage, sc.duringMessage, sc.afterMessage, start, end)
  }
}