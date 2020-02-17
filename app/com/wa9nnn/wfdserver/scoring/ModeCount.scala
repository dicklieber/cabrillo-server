
package com.wa9nnn.wfdserver.scoring

import com.wa9nnn.wfdserver.htmlTable.{Row, RowSource}

case class ModeCount(mode: String, count: Int) extends RowSource with Ordered[ModeCount] {
  def multipier: Int = mode match {
    case "PH" => 1
    case _ => 2
  }

  def points: Int = count * multipier

  override def toRow: Row = Row(mode, points, multipier, s"$count QSOs × $multipier per.")

  override def compare(that: ModeCount): Int = this.mode compareToIgnoreCase that.mode
}

