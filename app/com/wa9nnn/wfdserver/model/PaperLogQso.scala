
package com.wa9nnn.wfdserver.model

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate, LocalDateTime, LocalTime}

import com.wa9nnn.wfdserver.util.TimeConverters.zoneIdUtc
import com.wa9nnn.wfdserver.htmlTable.{Cell, Header, Row, RowSource}
import controllers.routes
import PaperLogQso._
import com.wa9nnn.wfdserver.util.TimeConverters

/**
 *
 * @param index this is not persisted. It's updated while begin read from the qsos.txt file.
 */
case class PaperLogQso(freq: String = "", mode: String = "DI", date: LocalDate = LocalDate.ofEpochDay(1), time: LocalTime = LocalTime.MIDNIGHT,
                       theirCall: CallSign = CallSign.empty,
                       category: String = "", section: String = "",
                       callSign: CallSign = CallSign.empty,
                       index: Int = createIndex) extends RowSource {
  def isCreate: Boolean = index == createIndex

  def instant: Instant = {
    LocalDateTime.of(date, time).atZone(TimeConverters.zoneIdUtc).toInstant
  }


  def next: PaperLogQso = copy(time = LocalTime.MIN, theirCall = CallSign.empty, category = "", section = "")

  def withIndex(index: Int): PaperLogQso = {
    if (this.index != createIndex) {
      throw new IllegalStateException(s"Qso already has index of ${this.index}")
    }
    copy(index = index)
  }

  def toRow(editingThisRow: Boolean = false): Row = {
    val sIndex = index.toString
    val rr = Row(
      Cell("DeleteQso").withCssClass("deleteQso")
        .withImage(routes.Assets.versioned("images/delete.png").url)
        .withId(sIndex + "|" + callSign.toString)
        .withToolTip("Remove this QSO, Can't be undone!"),
      Cell("EditQso").withUrl(routes.PaperLogController.editQso(callSign, index).url)
        .withImage(routes.Assets.versioned("images/pencil.png").url)
        .withToolTip("Update this qso row."),
      sIndex,
      freq,
      mode,
      Cell(date),
      time,
      theirCall,
      category,
      section
    ).withId(sIndex)
    if (editingThisRow)
      rr.withCssClass("editing")
    else {
      rr
    }
  }

  override def toRow: Row = toRow()

  def toCsvLine: String = {
    Seq(
      freq,
      mode,
      date.toString,
      timeFormatter.format(time),
      theirCall,
      category,
      section,
      callSign.toString
    ).mkString(",")
  }
}

object PaperLogQso {
  def fromCsv(line: String, index: Int): PaperLogQso = {
    val e: Array[String] = line.split(",")
    PaperLogQso(
      freq = e(0),
      mode = e(1),
      date = LocalDate.parse(e(2)),
      time = LocalTime.parse(e(3), timeFormatter),
      theirCall = e(4),
      category = e(5),
      section = e(6),
      callSign = e(7),
      index = index)
  }

  def apply(freq: String = "", mode: String = "DI", date: LocalDate = LocalDate.ofEpochDay(1), time: LocalTime = LocalTime.MIDNIGHT,
            theirCall: CallSign = CallSign.empty,
            category: String = "", section: String = "",
            callSign: CallSign = CallSign.empty,
            index: Int = createIndex): PaperLogQso = {
    new PaperLogQso(freq, mode.toUpperCase, date, time, theirCall, category.toUpperCase, section.toUpperCase, callSign, index)
  }

  def apply(callSign: CallSign) = new PaperLogQso(callSign = callSign)


  def header(rangeInfo: String): Header = {
    Header(s"QSOs ($rangeInfo)", "Delete", "Edit", "Index", "Freq", "Mode", "Date", "Time", "CallSign", "Cat", "Sect")
  }

  val createIndex: Int = -1
  val timeFormatter: DateTimeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm").withZone(zoneIdUtc)
}

