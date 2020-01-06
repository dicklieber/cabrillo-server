
package org.wa9nnn.wfdserver.scoring

import java.util.concurrent.atomic.AtomicInteger

import org.wa9nnn.wfdserver.db.mongodb.QSO
import org.wa9nnn.wfdserver.htmlTable._

import scala.collection.concurrent.TrieMap

class QsoAccumulator {
  private val modeBands = Set.newBuilder[ModeBand]
  private val modeCountMap = new TrieMap[String, AtomicInteger]()
  private val bandCountMap = new TrieMap[String, AtomicInteger]()

  def apply(qso: QSO): Unit = {
    val modeBand = qso.modeBand
    modeBands += modeBand
    modeCountMap.getOrElseUpdate(modeBand.mode, new AtomicInteger()).incrementAndGet()
    bandCountMap.getOrElseUpdate(modeBand.band, new AtomicInteger()).incrementAndGet()
  }

  def result(powerMultiplier: Int): QsoResult = {
    QsoResult(
      byBand = bandCountMap.map { case (band, ai) => BandCount(band, ai.get()) }.toSeq,
      byMode = modeCountMap.map { case (mode, ai) => ModeCount(mode, ai.get()) }.toSeq,
      modeBand = modeBands.result().toSeq
    )
  }
}

case class BandCount(band: String, count: Int) extends RowSource with Ordered[BandCount] {
  override def toRow: Row = Row(Cell(band).withColSpan(2), count)

  override def compare(that: BandCount): Int = this.band compareToIgnoreCase that.band
}

case class ModeCount(mode: String, count: Int) extends RowSource with Ordered[ModeCount] {
  def points: Int = mode match {
    case "PH" => count
    case _ => 2 * count
  }

  override def toRow: Row = Row(mode, points, count)

  override def compare(that: ModeCount): Int = this.mode compareToIgnoreCase that.mode
}


case class QsoResult(byBand: Seq[BandCount], byMode: Seq[ModeCount], modeBand: Seq[ModeBand])  {
  def bandModeMultiplier: Int = modeBand.length

  def qsoPoints: Int = byMode.foldLeft(0) { case (accum: Int, modeCount) =>
    accum + ( modeCount.mode match {
      case "PH" => modeCount.count
      case _ => modeCount.count * 2
    })

  }

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

    val r = (new SectionedRowCollector()
      .+=("By Mode", Seq("Mode", "Award", "Count"), byMode.sorted.map(_.toRow))
      .+=(Cell("By Band"), Seq(Cell("Band").withColSpan(2), "Count"), byBand.sorted.map(_.toRow)))
      .+=(Cell("Totals"), Seq("Item", "Award", "Stuff", "Explain"), totalsRows)

    //    val r = (new SectionedRowCollector).rows
    r.rows
  }
}

object QsoResult {
  val header: Header = Header("Qsos", "Item", "Value", "Points")
}

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


