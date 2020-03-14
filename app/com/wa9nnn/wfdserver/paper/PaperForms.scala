
package com.wa9nnn.wfdserver.paper

import com.wa9nnn.wfdserver.model.{CallSign, PaperLogQso}
import javax.inject.{Inject, Singleton}
import play.api.data.{Form, Mapping}
import play.api.data.Forms._
import com.wa9nnn.wfdserver.play.EnumPlayUtils.`enum`

/**
 * Maps fields in  Play form to domain case classes.
 *
 * @param sectionValidator  validates arrl section values.
 * @param categoryValidator validates WFD categories.
 */
@Singleton
class PaperForms @Inject()(
                  sectionValidator: SectionValidator,
                  categoryValidator: CategoryValidator,

                ) {

  private val callSign: Mapping[CallSign] = of[CallSign]

  val header: Form[PaperLogHeader] = Form(
    mapping(
      "callSign" -> callSign,
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
        .verifying(sectionValidator.sectionConstraint),
      "txPower" -> enum(TxPower),
      "noMainPower" -> boolean,
      "awayFromHome" -> boolean,
      "outdoors" -> boolean,
      "satellite" -> boolean
    )(PaperLogHeader.apply)(PaperLogHeader.unapply)
  )

  val qso: Form[PaperLogQso] = Form(
    mapping(
      "freq" -> nonEmptyText,
      "mode" -> nonEmptyText,
      "date" -> localDate,
      "time" -> localTime("HH:mm"),
      "theirCall" -> callSign,
      "cat" -> nonEmptyText
        .verifying(categoryValidator.categoryConstraint),
      "sect" -> nonEmptyText
        .verifying(sectionValidator.sectionConstraint),
      "callSign" -> callSign,
      "index" -> number
    )(PaperLogQso.apply)(PaperLogQso.unapply)
  )

}