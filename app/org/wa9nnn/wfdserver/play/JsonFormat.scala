
package org.wa9nnn.wfdserver.play

import org.wa9nnn.cabrillo.Result
import org.wa9nnn.cabrillo.model.CabrilloData
import org.wa9nnn.cabrillo.requirements.CabrilloError
import play.api.libs.json.{Format, Json}

object JsonFormat {
  implicit val errorAtLineFormat: Format[CabrilloError] = Json.format[CabrilloError]
  implicit val resultFormat: Format[Result] = Json.format[Result]

}
