
package org.wa9nnn.wfdserver.scoring

import org.wa9nnn.wfdserver.htmlTable.{Cell, Header, MultiColumn, Row, SectionedRowCollector, TableInACell}

case class QsoResult( byBand: Seq[BandCount], byMode: Seq[ModeCount], modeBand: Seq[ModeBand], qsoPoints:Int)  {
  def qsos:Int = byMode.length
  def bandModeMultiplier: Int = modeBand.length
//todo qsoPoints needs to be in QsoResult?
//  def qsoPoints: Int = byMode.foldLeft(0) { case (accum: Int, modeCount) =>
//    accum + ( modeCount.mode match {
//      case "PH" => modeCount.count
//      case _ => modeCount.count * 2
//    })

//  }

  def toRows(soapBoxTotal:Int): Seq[Row] = {

    val multiplier =  modeBand.length
    val multipliers: Cell = TableInACell(MultiColumn(modeBand.sorted.map(_.toCell), 4).withCssClass("multiplierTable"))

    val qsoPointsRow = Row("QSO Points", qsoPoints, "", "Points before multiplier")
    val multiplierRow = Row("Multiplier", "", multiplier, multipliers).withToolTip("Each combination of mode and band is one multiplier.")
    val multipliedPoints = multiplier * qsoPoints
    val multipliedTotal = Row("Multiplied", multipliedPoints, "", "Raw QSO points times multiplier.")
    val actualScore = multipliedPoints + soapBoxTotal
    val grandTotal = Row("Grand", actualScore, "", "Soapbox claims plus multiplied QSO total.")

    val totalsRows = Seq(qsoPointsRow, multiplierRow, multipliedTotal, grandTotal)

    val r = new SectionedRowCollector()
      .+=("By Mode", Seq("Mode", "Award", "Count"), byMode.sorted.map(_.toRow))
      .+=(Cell("By Band"), Seq(Cell("Band").withColSpan(2), "Count"), byBand.sorted.map(_.toRow))
      .+=(Cell("Totals"), Seq("Item", "Award", "Stuff", "Explain"), totalsRows)

    //    val r = (new SectionedRowCollector).rows
    r.rows
  }
}

object QsoResult {
  val header: Header = Header("Qsos", "Item", "Value", "Points")
}

