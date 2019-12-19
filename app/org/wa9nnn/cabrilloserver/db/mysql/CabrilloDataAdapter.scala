
package org.wa9nnn.cabrilloserver.db.mysql

import java.sql.Time
import java.time.Instant

import com.typesafe.scalalogging.LazyLogging
import org.wa9nnn.cabrillo.model.CabrilloData
import org.wa9nnn.cabrillo.model.CabrilloTypes.Tag
import org.wa9nnn.cabrillo.parsers.QSO_WFD
import org.wa9nnn.cabrilloserver.db.mysql.Tables._

/**
 * Knows how to adapt a [[CabrilloData]] to the case classes need to interact with a SQL database.
 *
 * @param cabrilloData from file.
 */
case class CabrilloDataAdapter(cabrilloData: CabrilloData) extends LazyLogging{
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

  implicit def asDate(stamp: Instant): java.sql.Date = {
    new java.sql.Date(stamp.toEpochMilli)
  }
  implicit def asTime(stamp: Instant): java.sql.Time = {
    new java.sql.Time(stamp.toEpochMilli)
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
    timeId = TimeId("CATEGORY-TIME"),
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

  def contactsRows(entryId: Int): Seq[ContactsRow] = {
    def row(qso: QSO_WFD): ContactsRow = {
      logger.info(s"qsoMode: ${qso.mode}")
      ContactsRow(
        id = 0, // autoinc
        entryId = entryId,
        freq = qso.freq,
        qsoMode = Mode(qso.mode),
        contactDate = qso.stamp,
        contactTime = qso.stamp,
        callsign = qso.sent.callsign,
        exch = qso.received.toString(),
        transmitter = 0 //todo
      )
    }
    //todo soapbox


    cabrilloData.apply("QSO").flatMap { tv =>
      tv match {
        case qso: QSO_WFD =>
          Seq(row(qso))
        case _ =>
          Seq.empty
      }

    }
  }

}


