package org.wa9nnn.wfdserver.db

import org.wa9nnn.wfdserver.htmlTable.{Header, Row, RowsSource, Table}

/**
 * All info about an entry as html [[Table]]s that can be rendered on an HTML page.
 *
 */
case class EntryViewData(stationLog: RowsSource, contacts: Seq[Row], callSign: String, club: Option[String]) {

  val entryTable: Table = {
    Table(Header("Submitted Log", "Tag", "Value"), stationLog.toRows(): _*).withCssClass("resultTable")
  }

  val contactsTable: Table = {
    Table(Qso.header(contacts.length), contacts:_*).withCssClass("resultTable")
  }
}
