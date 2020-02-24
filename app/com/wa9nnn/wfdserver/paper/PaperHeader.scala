package com.wa9nnn.wfdserver.paper

import java.time.{LocalDate, LocalTime}

import com.wa9nnn.wfdserver.model.WfdTypes.CallSign
import com.wa9nnn.wfdserver.paper.TxPower.TxPower

case class PaperHeader(callSign: String = "",
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
                      )

object TxPower extends Enumeration {
  type TxPower = Value
  val qrp: TxPower.Value = Value("qrp")
  val medium: TxPower.Value = Value("medium")
  val high: TxPower.Value = Value("high")

  //  implicit val myEnumReads = Reads.enumNameReads(MyEnum)
  //  implicit val myEnumWrites = Writes.enumNameWrites
}

case class PaperQSO(freq: String, localDate: LocalDate, localTime: LocalTime, mode: String, callSign: CallSign, catSect: String)