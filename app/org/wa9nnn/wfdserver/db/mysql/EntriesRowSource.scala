package org.wa9nnn.wfdserver.db.mysql

import org.wa9nnn.wfdserver.db.mysql
import org.wa9nnn.wfdserver.db.mysql.Tables.EntriesRow
import org.wa9nnn.wfdserver.htmlTable.{Row, RowsSource}

/**
 * All info about an entry
 *
 */
case class EntriesRowSource(entriesRow: EntriesRow, soapboxes: Seq[String]) extends RowsSource {
  private val e: mysql.Tables.EntriesRow = entriesRow
  private val soapBoxRows: Seq[Row] = soapboxes.map(Row("SOAPBOX", _))
  private val rows = Seq(
    Row("entryId", e.id),
    Row("logVersion", e.logVersion),
    Row("callsign", e.callsign),
    Row("contest", e.contest),
    Row("assisted", Assisted(e.assistedId)),
    Row("band", Band(e.bandId)),
    Row("mode", Mode(e.modeId)),
    Row("operators", e.operators),
    Row("operatorType", OperatorType(e.operatorTypeId)),
    Row("power", Power(e.powerId)),
    Row("time", Time(e.timeId)),
    Row("transmitter", Transmitter(e.transmitterId)),
    Row("overlay", Overlay(e.overlayId)),
    Row("certificate", e.certificate),
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
  ) ++ soapBoxRows

  override def toRows(includeNone: Boolean, prefix: String): Seq[Row] = {
    rows
  }
}
