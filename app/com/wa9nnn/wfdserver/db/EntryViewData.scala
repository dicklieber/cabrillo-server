package com.wa9nnn.wfdserver.db

import com.wa9nnn.wfdserver.htmlTable.{Header, Row, Table}
import com.wa9nnn.wfdserver.model.{LogInstance, Qso}

/**
 * All info about an entry as html [[Table]]s that can be rendered on an HTML page.
 *
 */
case class  EntryViewData(logInstance: LogInstance) {

  private val qsos: Seq[Qso] = logInstance.qsos
  val entryTable: Table = {
    val rows = logInstance.stationLog.toRows()
    val r = rows :+ Row("id", logInstance.id)
      .withToolTip("With momgoDB this is the callSign, for MySQL this is a autoinc integer.")

    Table(Header("Submitted Log", "Tag", "Value"), r).withCssClass("resultTable")
  }

  val contactsTable: Table = {
     Table(Qso.header(logInstance.qsoCount), qsos.map(_.toRow)).withCssClass("resultTable")
  }

  def title:String = {
    s"${logInstance.stationLog.callSign} ${logInstance.stationLog.club.getOrElse("")}"
  }
}
