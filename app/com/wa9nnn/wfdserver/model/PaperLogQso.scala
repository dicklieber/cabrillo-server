
package com.wa9nnn.wfdserver.model

import java.time.{LocalDate, LocalTime}

import com.wa9nnn.wfdserver.htmlTable.{Cell, Header, Row, RowSource}
import controllers.routes

/**
 *
 * @param freq
 * @param mode
 * @param date
 * @param time
 * @param theirCall
 * @param category
 * @param section
 * @param callSign
 * @param index this is not persisted.
 */
case class PaperLogQso(freq: String = "", mode: String = "DI", date: LocalDate = LocalDate.ofEpochDay(1), time: LocalTime = LocalTime.MIDNIGHT,
                       theirCall: CallSign = CallSign.empty,
                       category: String = "", section: String = "",
                       callSign: CallSign = CallSign.empty,
                       index: Int = 0) extends RowSource {
  def next: PaperLogQso = copy(time = LocalTime.MIN, theirCall = CallSign.empty, category = "", section = "")

  def toCsvLine: String = {
    productIterator
      .toSeq
      .init
      .map(_.toString).mkString(",")
  }

  def withIndex(index: Int): PaperLogQso = copy(index = index)

  override def toRow: Row = {
    Row(
      //      Cell("DeleteQso").withUrl(routes.PaperLogController.deleteQso(theirCall, index).url)
      Cell("DeleteQso").withCssClass("deleteQso")
        .withImage(routes.Assets.versioned("images/delete.png").url)
        .withId(index.toString + "|" + callSign.toString)
        .withToolTip("Remove this QSO, Can't be undone!"),
      Cell("EditQso").withUrl(routes.PaperLogController.editQso(callSign, index).url)
        .withImage(routes.Assets.versioned("images/pencil.png").url)
        .withToolTip("Update this qso row."),
      freq,
      mode,
      Cell(date),
      time,
      theirCall,
      category,
      section
    )
  }
}

object PaperLogQso {
  def apply(freq: String = "", mode: String = "DI", date: LocalDate = LocalDate.ofEpochDay(1), time: LocalTime = LocalTime.MIDNIGHT,
            theirCall: CallSign = CallSign.empty,
            category: String = "", section: String = "",
            callSign: CallSign = CallSign.empty,
            index: Int = 0): PaperLogQso = {
    new PaperLogQso(freq, mode.toUpperCase, date, time, theirCall, category.toUpperCase, section.toUpperCase, callSign, index)
  }

  def apply(callSign: CallSign) = new PaperLogQso(callSign = callSign)

  def fromCsv(line: String, index: Int): PaperLogQso = {
    val e: Array[String] = line.split(",")
    PaperLogQso(freq = e(0),
      mode = e(1),
      date = LocalDate.parse(e(2)),
      time = LocalTime.parse(e(3)),
      theirCall = e(4),
      category = e(5),
      section = e(6),
      callSign = e(7),
      index = index)
  }

  def header(qsoCount: Int): Header = {
    Header(f"QSOs ($qsoCount%,d)", "Delete", "Edit", "Freq", "Mode", "Date", "Time", "CallSign", "Cat", "Sect")
  }

  //  def tupled: ((String, String, LocalDate, LocalTime, CallSign, String, String, CallSign)) => PaperLogQso = (PaperLogQso.apply _).tupled
}

