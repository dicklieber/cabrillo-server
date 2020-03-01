
package com.wa9nnn.wfdserver.model

import java.time.{LocalDate, LocalTime}

import com.wa9nnn.wfdserver.htmlTable.{Cell, Header, Row, RowSource}

case class PaperLogQso(freq: String = "", mode: String = "DI", date: LocalDate = LocalDate.ofEpochDay(1), time: LocalTime = LocalTime.MIDNIGHT,
                       theirCall: CallSign = CallSign.empty,
                       category: String = "", section:String = "",
                       callSign: CallSign = "") extends RowSource {
  def next: PaperLogQso = copy(time = LocalTime.MIN, theirCall = CallSign.empty, category = "", section="")

  def toCsvLine: String = {
    productIterator.map(_.toString).mkString(",")
  }

  override def toRow: Row = {
    Row(
      freq,
      Cell(date),
      mode,
      time,
      theirCall,
      category,
      section
    )
  }
}

object PaperLogQso {
  def fromCsv(line: String, callSign: CallSign): PaperLogQso = {
    val e: Array[String] = line.split(",")
    PaperLogQso(e(0),
      e(1),
      LocalDate.parse(e(2)),
      LocalTime.parse(e(3)),
      e(4),
      e(5),
      e(6),
      callSign)
  }

  def header(qsoCount: Int): Header = {
    Header(f"QSOs ($qsoCount%,d)", "Freq", "Mode", "Date", "Time", "CallSign", "Cat","Sect")
  }
}

