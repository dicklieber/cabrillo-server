
package org.wa9nnn.wfdserver.db.mysql

import com.typesafe.scalalogging.LazyLogging
import org.wa9nnn.wfdserver
import org.wa9nnn.wfdserver.db.mysql.Tables._
import org.wa9nnn.wfdserver.db.{Categories, LogInstance, StationLog}

/**
 * Knows how to adapt a [[LogInstance]] to the  slick generated case classes needed to interact with a SQL database.
 *
 * @param logInstance from file.
 */
case class MySQLDataAdapter(logInstance: LogInstance) extends LazyLogging {

  private val stationLog: StationLog = logInstance.stationLog
  private val categories: Categories = logInstance.stationLog.categories

  /**
   * Most of the columns are optional with in [[EntriesRow]] is a scala Optional
   * the opt method will get the 1st named tag and wrap in an Option
   *
   * @param logVersion None if there is no existing entry for this callsign otherwise Some(highest logVersion in db)
   * @return
   */
  def entryRow(logVersion: Option[Int]): EntriesRow = EntriesRow(
    id = 0, // will come from DB
    logVersion = logVersion.getOrElse(-1) + 1,
    callsign = stationLog.callSign,
    contest = "WFD", //todo do we need this in LogInstance
    assistedId = categories.assisted.contains("ASSISTED"),
    bandId = Band(categories.band).getOrElse(0),
    modeId = Mode(categories.mode).getOrElse(0),
    operators = categories.operator.map(_.split(" ").length).getOrElse(0),
    operatorTypeId = OperatorType(categories.operator).getOrElse(0),
    powerId = Power(categories.power).getOrElse(0),
    stationId = Station(categories.station).getOrElse(0),
    timeId = Time(categories.time).getOrElse(0),
    transmitterId = Transmitter(categories.transmitter).getOrElse(0), //"CATEGORY-TRANSMITTER"),
    overlayId = Overlay(categories.overlay).getOrElse(0),
    certificate = stationLog.certificate.contains("YES"), // ("CERTIFICATE", "YES"),
    claimedScore = stationLog.claimedScore.getOrElse(0),
    club = stationLog.club.getOrElse(""),
    createdBy = stationLog.createdBy.getOrElse(""),
    email = stationLog.email.getOrElse(""),
    gridLocator = stationLog.gridLocator.getOrElse(""),
    location = stationLog.location.getOrElse(""),
    name = stationLog.name.getOrElse(""),
    address = stationLog.address.map(_.mkString("\n").take(75)).headOption.getOrElse(""),
    city = stationLog.city.getOrElse(""),
    stateProvince = stationLog.stateProvince.getOrElse(""),
    postalcode = stationLog.postalCode.getOrElse(""),
    country = stationLog.country.getOrElse(""),
    arrlSection = stationLog.arrlSection.getOrElse(""),
    category = stationLog.location.getOrElse("")
  )

  def contactsRows(entryId: Int): Seq[ContactsRow] = {
    def row(qso: org.wa9nnn.wfdserver.db.Qso): ContactsRow = {
      val strings = qso.s.ex.split(" ")
      ContactsRow(
        id = 0, // autoinc
        entryId = entryId,
        freq = qso.b,
        qsoMode = Mode(qso.m),
        contactDate = new java.sql.Date(qso.ts.toEpochMilli),
        contactTime = new java.sql.Time(qso.ts.toEpochMilli),
        callsign = qso.r.cs,
        exch = qso.r.ex,
        transmitter = 0,
        category = strings(0),
        sect = strings(1) //todo how parse this
      )
    }

    logInstance.qsos.flatMap { qso =>

      Seq(row(qso))
    }
  }

  def soapboxes(entryId: Int): Iterable[wfdserver.db.mysql.Tables.SoapboxesRow] = {
    for {
      soapbox <- stationLog.soapBoxes
      body = soapbox.trim
      if !body.isEmpty
    } yield {
      SoapboxesRow(0, entryId, body)
    }
  }
}




