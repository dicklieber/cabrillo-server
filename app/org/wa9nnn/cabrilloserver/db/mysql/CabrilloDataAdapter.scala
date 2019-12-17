
package org.wa9nnn.cabrilloserver.db.mysql

import org.wa9nnn.cabrillo.model.CabrilloData
import org.wa9nnn.cabrillo.model.CabrilloTypes.Tag
import org.wa9nnn.cabrilloserver.db.mysql.Tables._

/**
 * Knows how to adapt a [[CabrilloData]] to the case classes need to interact with a SQL database.
 *
 * @param cabrilloData from file.
 */
case class CabrilloDataAdapter(cabrilloData: CabrilloData) {
  private implicit val cd = cabrilloData

  /**
   *
   * @param tag name
   * @return body of 1st instance of the [[Tag]] or None if tag not present
   */
  private implicit def str(tag: Tag): Option[String] = {
    cabrilloData.apply(tag).headOption.map(_.body)
  }

  private implicit def bol(t: (Tag, String)): Option[Boolean] = {
    cabrilloData.apply(t._1).headOption.map(_.body.toUpperCase() == t._2)
  }

  private implicit def int(tag: Tag): Option[Int] = {
    cabrilloData.apply(tag).headOption.map(_.body.toInt)
  }

  /**
   * Most of the columns are optional with in [[EntriesRow]] is a scala Optional
   * the opt method will get the 1st named tag and wrap in an Option
   *
   * @return
   */
  def entryRow: EntriesRow = EntriesRow(
    id = 0, // will come from DB
    logVersion = str("START-OF-LOG").map(_.toDouble.toInt),
    callsign = "CALLSIGN",
    contest = "CONTEST",
    assisted = ("CATEGORY-ASSISTED", "ASSISTED"),
    bandId = Band("CATEGORY-BAND"),
    modeId = Mode("CATEGORY-MODE"),
    operators = str("OPERATORS").map(_.split(" ").length),
    operatorTypeId = Operator("CATEGORY-OPERATOR"),
    powerId = Power("CATEGORY-POWER"),
    stationId = Station("CATEGORY-STATION"),
    timeId = Time("CATEGORY-TIME"),
    transmitterId = Transmitter("CATEGORY-TRANSMITTER"),
    overlayId = Overlay("CATEGORY-OVERLAY"),
    certificate = ("CERTIFICATE", "YES"),
    claimedScore = "CLAIMED-SCORE",
    club = "CLUB",
    createdBy = "CREATED-BY",
    email = "EMAIL",
    gridLocator = "GRID-LOCATOR",
    location = "LOCATION",
    name = "NAME",
    address = Address(cabrilloData),
    city = "ADDRESS-CITY",
    stateProvince = "ADDRESS-STATE-PROVINCE",
    postalcode = "ADDRESS-POSTALCODE",
    country = "ADDRESS-COUNTRY"
  )

  def contactsRows: Seq[ContactsRow] = {
    Seq.empty //todo
  }

}


