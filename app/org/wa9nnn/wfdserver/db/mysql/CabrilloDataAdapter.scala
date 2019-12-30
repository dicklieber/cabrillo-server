
package org.wa9nnn.wfdserver.db.mysql

import com.typesafe.scalalogging.LazyLogging
import org.wa9nnn.cabrillo.model.CabrilloData
import org.wa9nnn.cabrillo.parsers.QSO_WFD
import org.wa9nnn.wfdserver
import org.wa9nnn.wfdserver.db.Adapter
import org.wa9nnn.wfdserver.db.mysql.Tables._
import org.wa9nnn.wfdserver.db.mysql.CabrilloDataAdapter._
/**
 * Knows how to adapt a [[CabrilloData]] to the case classes need to interact with a SQL database.
 *
 * @param cabrilloData from file.
 */
case class CabrilloDataAdapter(override val cabrilloData: CabrilloData) extends Adapter() with LazyLogging {

  /**
   * Most of the columns are optional with in [[EntriesRow]] is a scala Optional
   * the opt method will get the 1st named tag and wrap in an Option
   *
   * @param logVersion None if there is no existing entry for this callsign otherwise Some(highest logVersion in db)
   * @return
   */
  def entryRow(logVersion: Option[Int]): EntriesRow = EntriesRow(
    id = 0, // will come from DB
    logVersion = Some(logVersion.getOrElse(-1) + 1),
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
    overlayId = Overlay(str("CATEGORY-OVERLAY")),
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

  def contactsRows(entryId: Int): Seq[ContactsRow] = {
    def row(qso: QSO_WFD): ContactsRow = {
      ContactsRow(
        id = 0, // autoinc
        entryId = entryId,
        freq = qso.freq,
        qsoMode = Mode(qso.mode),
        contactDate = qso.stamp,
        contactTime = qso.stamp,
        callsign = qso.sent.callsign,
        exch = qso.sent.category + " " + qso.sent.section + exchSeperator + qso.received.toString(),
        transmitter = 0 //todo how parse this

      )
    }

    cabrilloData.apply("QSO").flatMap { tv =>
      tv match {
        case qso: QSO_WFD =>
          Seq(row(qso))
        case _ =>
          Seq.empty
      }
    }
  }

  def soapboxes(entryId: Int): Iterable[wfdserver.db.mysql.Tables.SoapboxesRow] = {
    for {
      soapbox <- cabrilloData("SOAPBOX")
      body = soapbox.body.trim
      if !body.isEmpty
    } yield {
      SoapboxesRow(0, Some(entryId), Some(body))
    }
  }
}

object CabrilloDataAdapter {
  val exchSeperator = '|'
}


