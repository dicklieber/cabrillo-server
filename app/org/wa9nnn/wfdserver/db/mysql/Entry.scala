package org.wa9nnn.wfdserver.db.mysql

import java.time.LocalDateTime

import org.wa9nnn.wfdserver.db.mysql
import org.wa9nnn.wfdserver.db.mysql.Tables.{ContactsRow, EntriesRow}
import org.wa9nnn.wfdserver.htmlTable.{Header, Row, Table}

/**
 * All info about an entry
 */
case class Entry(entriesRow: EntriesRow, soapboxes: Seq[String], contacts: Seq[ContactsRow]) {
  private val e: mysql.Tables.EntriesRow = entriesRow

  def callsign:String = e.callsign.getOrElse("??")
  def club:String = e.club.getOrElse("")
  val entryTable: Table = {

    Table(Header("Submitted Log", "Tag", "Value"),

      Row("entryId", e.id),
      Row("logVersion", e.logVersion),
      Row("callsign", e.callsign),
      Row("contest", e.contest),
      Row("assisted", e.assisted),
      Row("band", Band(e.bandId, "??")),
      Row("mode", Mode(e.modeId, "??")),
      Row("operators", e.operators.getOrElse("??")),
      Row("operatorType", Operator(e.operatorTypeId, "??")),
      Row("power", Power(e.powerId, "??")),
      Row("time", Time(e.timeId, "??")),
      Row("transmitter", Transmitter(e.transmitterId, "??")),
      Row("overlay", Overlay(e.overlayId, "??")),
      Row("certificate", e.certificate.getOrElse(false)),
      Row("claimedScore", e.claimedScore),
      Row("club", e.club),
      Row("createdBy", e.createdBy),
      Row("gridLocator", e.gridLocator),
      Row("location", e.location),
      Row("address", e.address),
      Row("city", e.city),
      Row("stateProvince", e.stateProvince),
      Row("postalcode", e.postalcode),
      Row("country", e.country)
    ).withCssClass("resultTable")
  }

  val soapBoxesTable: Table = {
    Table(s"SOAPBOXES (${soapboxes.length})", soapboxes).withCssClass("resultTable")
  }

  val contactsTable: Table = {
    Table(Header(s"Contacts (${contacts.length})", "freq", "mode", "stamp", "callsign", "exch", "transmitter"),
      contacts.map { c =>
        val instant = LocalDateTime.of(c.contactDate.toLocalDate, c.contactTime.toLocalTime)
        Row(c.freq, Mode(c.qsoMode), instant, c.callsign, c.exch, c.transmitter)
      }: _*
    ).withCssClass("resultTable")

  }
}
