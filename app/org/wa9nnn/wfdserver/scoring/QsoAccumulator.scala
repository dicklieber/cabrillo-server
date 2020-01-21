
package org.wa9nnn.wfdserver.scoring

import java.util.concurrent.atomic.AtomicInteger

import org.wa9nnn.wfdserver.htmlTable._

import scala.collection.concurrent.TrieMap

class QsoAccumulator {
  private val modeBands = Set.newBuilder[ModeBand]
  private val modeCountMap = new TrieMap[String, AtomicInteger]()
  private val bandCountMap = new TrieMap[String, AtomicInteger]()
  private var qsoScore: Int = 0
  private var errantQsos: Seq[QsoKind] = Seq.empty

  def apply(qso: QsoBase): Unit = {
    qsoScore += qso.points
    val modeBand = qso.modeBand
    modeBands += modeBand
    modeCountMap.getOrElseUpdate(qso.mode, new AtomicInteger()).incrementAndGet()
    bandCountMap.getOrElseUpdate(qso.band, new AtomicInteger()).incrementAndGet()
    if (qso.qsoKind.isErrant) {
      errantQsos = errantQsos :+ qso.qsoKind
    }
  }

  def result(powerMultiplier: Int): QsoResult = {
    QsoResult(
      byBand = bandCountMap.map { case (band, ai) => BandCount(band, ai.get()) }.toSeq,
      byMode = modeCountMap.map { case (mode, ai) => ModeCount(mode, ai.get()) }.toSeq,
      modeBand = modeBands.result().toSeq,
      qsoScore * powerMultiplier
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


