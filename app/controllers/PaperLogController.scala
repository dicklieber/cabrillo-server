
package controllers

import be.objectify.deadbolt.scala.ActionBuilders
import com.typesafe.config.Config
import com.wa9nnn.wfdserver.auth.SubjectAccess
import com.wa9nnn.wfdserver.paper.{CategoryValidator, PaperHeader, SectionValidator, TxPower}
import com.wa9nnn.wfdserver.util.JsonLogging
import javax.inject.{Inject, Singleton}
import play.api.data.Forms._
import play.api.data.format.{Formats, Formatter}
import play.api.data.{Form, FormError, Mapping}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaperLogController @Inject()(cc: ControllerComponents, actionBuilder: ActionBuilders,
                                   sectionValidator: SectionValidator,
                                   categoryValidator: CategoryValidator)
                                  (implicit exec: ExecutionContext, config: Config)
  extends AbstractController(cc)
    with JsonLogging with play.api.i18n.I18nSupport
    with SubjectAccess {

  import controllers.EnumPlayUtils.enum

  private val formPaperHeader: Form[PaperHeader] = Form(
    mapping(
      "callSign" -> text,
      "club" -> text,
      "name" -> text,
      "email" -> nonEmptyText,
      "address" -> nonEmptyText,
      "city" -> nonEmptyText,
      "stateProvince" -> nonEmptyText,
      "postalCode" -> nonEmptyText,
      "country" -> nonEmptyText,
      "category" -> nonEmptyText
        .verifying(categoryValidator.categoryConstraint),
      "section" -> nonEmptyText
        .verifying(sectionValidator.passwordCheckConstraint),
      "txPower" -> enum(TxPower),
      "noMainPower" -> boolean,
      "awayFromHome" -> boolean,
      "outdoors" -> boolean,
      "satellite" -> boolean
    )(PaperHeader.apply)(PaperHeader.unapply)
  )

  def index(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() { implicit request =>

    val paperHeaderForm: Form[PaperHeader] = formPaperHeader.fill(new PaperHeader("WA9NNN"))
    Future(Ok(views.html.paperEditor(paperHeaderForm)))

  }

  def updateHeader(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() { implicit request =>

    Future {
      formPaperHeader.bindFromRequest.fold(
        formWithErrors => {
          BadRequest(views.html.paperEditor(formWithErrors))
        },
        contact => {
          //        val contactId = Contact.save(contact)
          Ok("ok todo")
          //        Redirect(views.html.paperEditor(contact.)).flashing("success" -> "Contact saved!")
        }
      )
    }
  }
}

object EnumPlayUtils {
  //------------------------------------------------------------------------
  // public
  //------------------------------------------------------------------------
  /**
   * Constructs a simple mapping for a text field (mapped as `scala.Enumeration`)
   *
   * For example:
   * {{{
   * Form("gender" -> enum(Gender))
   * }}}
   *
   * @param enum the enumeration
   */
  def enum[E <: Enumeration](enum: E): Mapping[E#Value] = of(enumFormat(enum))

  /**
   * Default formatter for `scala.Enumeration`
   *
   */
  def enumFormat[E <: Enumeration](enum: E): Formatter[E#Value] = new Formatter[E#Value] {
    def bind(key: String, data: Map[String, String]) = {
      Formats.stringFormat.bind(key, data).right.flatMap { s =>
        scala.util.control.Exception.allCatch[E#Value]
          .either(enum.withName(s))
          .left.map(e => Seq(FormError(key, "Must be one of: " + enum.values.mkString(","), Nil)))
      }
    }

    def unbind(key: String, value: E#Value) = Map(key -> value.toString)
  }
}