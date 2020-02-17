
package com.wa9nnn.wfdserver.scoring

import com.wa9nnn.wfdserver.htmlTable.{Cell, Row, RowSource}

case class BandCount(band: String, count: Int) extends RowSource with Ordered[BandCount] {
  override def toRow: Row = Row(Cell(band).withColSpan(2), count)

  override def compare(that: BandCount): Int = this.band compareToIgnoreCase that.band
}

