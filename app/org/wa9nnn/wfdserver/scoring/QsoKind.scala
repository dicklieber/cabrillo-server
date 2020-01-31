
package org.wa9nnn.wfdserver.scoring

import org.wa9nnn.cabrillo.requirements.Frequencies
import org.wa9nnn.wfdserver.util.JsonLogging

 case class QsoKind(points: Int, reason: String) {
  def isErrant: Boolean = points <= 0
}

 object QsoKind {
  val cw: QsoKind = QsoKind(2, "CW")
  val ph: QsoKind = QsoKind(1, "PH")
  val di: QsoKind = QsoKind(2, "DI")
  val missing: QsoKind = QsoKind(-1, "Missing")
  val unMatched: QsoKind = QsoKind(-1, "Unmatched")
  val timeDelta: QsoKind = QsoKind(-1, "TimeDelta")
  val badMode: QsoKind = QsoKind(0, "Bad Mode")
  val dup: QsoKind = QsoKind(0, "Dup")

  def mode(mode: String): QsoKind = mode match {
    case "CW" => QsoKind.cw
    case "DI" => QsoKind.di
    case "PH" => QsoKind.ph
    case x => badMode
  }
}

trait QsoBase {
  /**
   * Other official scoring-time checks
   * @return
   */
  def score()(implicit timeMatcher: TimeMatcher):QsoKind = QsoKind.mode(mode)

  def mode: String

  def band: String

  lazy val modeBand: ModeBand = ModeBand(mode, Frequencies.check(band))

}