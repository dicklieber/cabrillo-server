
package org.wa9nnn.wfdserver.scoring

import org.wa9nnn.cabrillo.requirements.Frequencies

case class QsoPoints(points: Int, reason: String)

object QsoPoints {
  val cw: QsoPoints = QsoPoints(2, "CW")
  val ph: QsoPoints = QsoPoints(1, "PH")
  val di: QsoPoints = QsoPoints(2, "DO")
  val missing: QsoPoints = QsoPoints(-1, "Missing")
  val unMatched: QsoPoints = QsoPoints(-1, "Unmatched")
  val timeDelta: QsoPoints = QsoPoints(-1, "TimeDelta")

  def mode(mode: String): QsoPoints = mode match {
    case "CW" => QsoPoints.cw
    case "DI" => QsoPoints.di
    case "PH" => QsoPoints.ph
    case x =>
      throw new IllegalArgumentException(s"Unknown mode: $x`")
  }
}

trait QsOPointer {
  def points:Int
  def mode:String
  def band:String
  lazy val modeBand: ModeBand = ModeBand(mode, Frequencies.check(band))

}