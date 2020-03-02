package com.wa9nnn.wfdserver.model

import java.time.Instant

import com.wa9nnn.wfdserver.htmlTable.RowsSource

/**
 * Non-QSO Cabrillo fields.
 */
case class StationLog(
                       callCatSect:CallCatSect,
                       club: Option[String],
                       createdBy: Option[String],
                       location: Option[String],
                       certificate: Option[String],
                       address:List[String],
                       city:Option[String],
                       stateProvince:Option[String],
                       postalCode: Option[String],
                       country : Option[String],
                       categories: Categories,
                       soapBoxes: List[String],
                       email: Option[String],
                       gridLocator: Option[String],
                       name: Option[String],
                       claimedScore: Option[Int],
                       logVersion:String,
                       ingested: Instant = Instant.EPOCH )extends RowsSource {
  def callSign:CallSign = callCatSect.callSign
}


case class CallCatSect(callSign:CallSign, category:String, arrlSection:String) extends RowsSource








