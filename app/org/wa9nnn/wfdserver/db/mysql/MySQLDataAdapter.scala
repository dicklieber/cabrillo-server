
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
    logVersion = Some(logVersion.getOrElse(-1) + 1),
    callsign = Some(stationLog.callSign),
    contest = Some("WFD"), //todo do we need this in LogInstance
    assisted = categories.assisted.map(_ == "ASSISTED"),
    bandId = Band(categories.band),
    modeId = Mode(categories.mode),
    operators = categories.operator.map(_.split(" ").length),
    operatorTypeId = Operator(categories.operator),
    powerId = Power(categories.power),
    stationId = Station(categories.station),
    timeId = Time(categories.time),
    transmitterId = Transmitter(categories.transmitter), //"CATEGORY-TRANSMITTER"),
    overlayId = Overlay(categories.overlay),
    certificate = stationLog.certificate.map(_ == "YES"), // ("CERTIFICATE", "YES"),
    claimedScore = stationLog.claimedScore,
    club = stationLog.club,
    createdBy = stationLog.createdBy,
    email = stationLog.email,
    gridLocator = stationLog.gridLocator,
    location = stationLog.location,
    name = stationLog.name,
    address = stationLog.address.map(_.mkString("\n").take(75)).headOption,
    city = stationLog.city,
    stateProvince = stationLog.stateProvince,
    postalcode = stationLog.postalCode,
    country = stationLog.country
  )

  def contactsRows(entryId: Int): Seq[ContactsRow] = {
    def row(qso: org.wa9nnn.wfdserver.db.Qso): ContactsRow = {
      ContactsRow(
        id = 0, // autoinc
        entryId = entryId,
        freq = qso.b,
        qsoMode = Mode(qso.m),
        contactDate = new java.sql.Date( qso.ts.toEpochMilli),
        contactTime = new  java.sql.Time( qso.ts.toEpochMilli),
        callsign = qso.r.cs,
        exch =  qso.r.ex,
        transmitter = 0 //todo how parse this
      )
    }

    logInstance.qsos.flatMap { qso =>

          Seq(row(qso))
      }
    }

  def soapboxes(entryId: Int): Iterable[wfdserver.db.mysql.Tables.SoapboxesRow] = {
    for {
      soapbox <-  stationLog.soapBoxes
      body = soapbox.trim
      if !body.isEmpty
    } yield {
      SoapboxesRow(0, Some(entryId), Some(body))
    }
  }
}




