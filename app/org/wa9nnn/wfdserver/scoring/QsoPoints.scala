
package org.wa9nnn.wfdserver.scoring

import org.wa9nnn.cabrillo.requirements.Frequencies
import org.wa9nnn.wfdserver.util.JsonLogging

case class QsoPoints(points: Int, reason: String)

object QsoPoints {
  val cw: QsoPoints = QsoPoints(2, "CW")
  val ph: QsoPoints = QsoPoints(1, "PH")
  val di: QsoPoints = QsoPoints(2, "DO")
  val missing: QsoPoints = QsoPoints(-1, "Missing")
  val unMatched: QsoPoints = QsoPoints(-1, "Unmatched")
  val timeDelta: QsoPoints = QsoPoints(-1, "TimeDelta")
  val badMode: QsoPoints = QsoPoints(0, "Bad Mode")

  def mode(mode: String): QsoPoints = mode match {
    case "CW" => QsoPoints.cw
    case "DI" => QsoPoints.di
    case "PH" => QsoPoints.ph
    case x => badMode
  }
}

trait QsOPointer {
  def points: Int

  def mode: String

  def band: String

  lazy val modeBand: ModeBand = ModeBand(mode, Frequencies.check(band))

}