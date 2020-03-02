package com.wa9nnn.wfdserver.paper

import java.time.{LocalDate, LocalTime}

import com.wa9nnn.wfdserver.model.CallSign
import com.wa9nnn.wfdserver.paper
import com.wa9nnn.wfdserver.paper.TxPower.TxPower
import play.api.libs.json.{Format, Json}

case class PaperLogHeader(callSign: CallSign = CallSign.empty,
                          club: String = "",
                          name: String = "",
                          email: String = "",
                          address: String = "", city: String = "", stateProvince: String = "", postalCode: String = "", country: String = "USA",
                          category: String = "", section: String = "",
                          txPower: TxPower = TxPower.high,
                          noMainPower: Boolean = false,
                          awayFromHome: Boolean = false,
                          outdoors: Boolean = false,
                          satellite: Boolean = false
                         ) extends EditableData {

  /**
   *
   * @return true if minimum data is present.
   */
  def isvalid: Boolean = {
    name.isEmpty ||
      email.isEmpty ||
      category.isEmpty ||
      section.isEmpty
  }
}

object TxPower extends Enumeration {
  type TxPower = Value
  val qrp: paper.TxPower.Value = Value("qrp")
  val medium: TxPower.Value = Value("medium")
  val high: TxPower.Value = Value("high")

  implicit val format2: Format[TxPower] = Json.formatEnum(this)
}

case class PaperQSO(freq: String, localDate: LocalDate, localTime: LocalTime, mode: String, callSign: CallSign, catSect: String)