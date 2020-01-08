package org.wa9nnn.wfdserver.model

import java.time.Instant

import org.wa9nnn.wfdserver.htmlTable.RowsSource

/**
 * Non-QSO Cabrillo fields.
 */
case class StationLog(
                       callSign: String,
                       club: Option[String],
                       createdBy: Option[String],
                       location: Option[String],
                       arrlSection: Option[String],
                       category: String,
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
                       ingested: Instant = Instant.now()) extends RowsSource










