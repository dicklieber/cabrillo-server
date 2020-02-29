
package com.wa9nnn.wfdserver.play

import com.wa9nnn.cabrillo.Result
import com.wa9nnn.cabrillo.requirements.CabrilloError
import play.api.libs.json.{Format, Json, OFormat}

object JsonFormat {
  import com.wa9nnn.wfdserver.paper.TxPower.TxPower

  implicit val errorAtLineFormat: Format[CabrilloError] = Json.format[CabrilloError]
  implicit val resultFormat: Format[Result] = Json.format[Result]

}
