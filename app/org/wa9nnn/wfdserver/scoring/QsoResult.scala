
package org.wa9nnn.wfdserver.scoring

import org.wa9nnn.wfdserver.htmlTable._
import org.wa9nnn.wfdserver.util.{Counted, CountedThings}

/**
 * Result of scoring all the [[org.wa9nnn.wfdserver.model.Qso]]s in a [[org.wa9nnn.wfdserver.model.StationLog]]
 * @param byBand     count for each band.
 * @param byMode     count for each mode.
 * @param modeBand   Mode/Band combinations.
 * @param qsoPoints  awarded Qso points. Before and multipliers.
 * @param errantQsos [[MatchedQso]]s that aren't really match or busted etc. (only for official score, provisional score only works with [[org.wa9nnn.wfdserver.model.Qso]]s.
 *                  which can never be errant.
 * @param allQsos all processed qsos.
 */
case class QsoResult(byBand: Seq[BandCount], byMode: Seq[ModeCount], modeBand: Seq[ModeBand], qsoPoints: Int, errantQsos:Seq[MatchedQso], allQsos: Seq[QsoBase], qsoKinds:CountedThings[QsoKind]) {
  def qsos: Int = allQsos.length

  def bandModeMultiplier: Int = modeBand.length

  def toRows(soapBoxTotal: Int): Seq[Row] = {

    val multiplier = modeBand.length
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

