package org.wa9nnn.wfdserver.db

import org.wa9nnn.wfdserver.db.mongodb.QSO
import org.wa9nnn.wfdserver.htmlTable.{Header, Row, RowsSource, Table}

/**
 * All info about an entry
 *
 */
case class EntryViewData(stationLog: RowsSource, contacts: Seq[Row], callSign: String, club: Option[String]) {

  val entryTable: Table = {
    Table(Header("Submitted Log", "Tag", "Value"), stationLog.toRows(): _*).withCssClass("resultTable")
  }

  val contactsTable: Table = {
    Table(QSO.header(contacts.length), contacts:_*).withCssClass("resultTable")
  }
}
