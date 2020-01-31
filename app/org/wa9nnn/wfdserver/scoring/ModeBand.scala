
package org.wa9nnn.wfdserver.scoring

import org.wa9nnn.wfdserver.htmlTable.Cell

/**
 *
 * @param mode DI, CW, PH etc.
 * @param band with frequencies converted to band
 */
case class ModeBand(mode: String, band: String) extends Ordered[ModeBand] {
  def toCell: Cell = Cell(s"$mode: $band")

  override def compare(that: ModeBand): Int = {
    this.toString compareToIgnoreCase that.toString
  }
}

