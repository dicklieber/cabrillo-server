
package com.wa9nnn.wfdserver.db.mysql

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.wfdserver
import com.wa9nnn.wfdserver.db.mysql.Tables._
import com.wa9nnn.wfdserver.model.{Categories, LogInstance, Qso, StationLog}
import com.wa9nnn.wfdserver.util.TimeConverters._

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
   * @return
   */
  def entryRow(): EntriesRow = EntriesRow(
    id = 0, // will come from DB
    logVersion = stationLog.logVersion.toFloat.toInt,
    callsign = stationLog.callSign.toString,
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
    address = {
     val r =  stationLog.address.mkString("\n")
        .take(75)
      r
    },
    city = stationLog.city.getOrElse(""),
    stateProvince = stationLog.stateProvince.getOrElse(""),
    postalcode = stationLog.postalCode.getOrElse(""),
    country = stationLog.country.getOrElse(""),
    arrlSection = stationLog.callCatSect.arrlSection,
    category = stationLog.callCatSect.category
  )


  def contactsRows(entryId: Int): Seq[ContactsRow] = {
    def row(qso: Qso): ContactsRow = {
      val strings = qso.s.ex.split(" ")

      val (sqlDate, sqlTime) = instantToSql(qso.ts)
      ContactsRow(
        id = 0, // autoinc
        entryId = entryId,
        freq = qso.b,
        qsoMode = Mode(qso.m),
        contactDate = sqlDate,
        contactTime = sqlTime,
        callsign = qso.r.cs.toString,
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




