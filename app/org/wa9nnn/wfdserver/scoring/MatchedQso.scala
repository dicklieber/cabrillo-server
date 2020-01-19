package org.wa9nnn.wfdserver.scoring

import org.wa9nnn.wfdserver.model.Qso
import org.wa9nnn.wfdserver.scoring.QsoPoints._

/**
 * that has been matched to th4 matching stations Qso. 
 * A qso that
 *
 * @param qso      of station being scored.
 * @param otherQso of station worked.
 */
case class MatchedQso(qso: Qso, otherQso: Option[Qso]) extends QsOPointer {
  /**
   * Checks that qso and otherQso, if present, matches the given callSign
   * and the two Qsos are consistent with each other.
   *
   * @param callSign owner.
   * @return true if belongs
   */
  def validate(callSign: String): Unit = {
    try {
      if (callSign != qso.sentCallSign) throw new QsoPairException(s"T1: qso.receivedCallSign: ${qso.receivedCallSign} != callSign: $callSign")

      otherQso.foreach { other =>
        if (other.receivedCallSign != callSign) throw new QsoPairException(s"T2: other.receivedCallSign: ${other.receivedCallSign} != callSign: $callSign")
        if (qso.sentCallSign != other.receivedCallSign) throw new QsoPairException(s"T4: qso.sentCallSign: ${qso.sentCallSign} other.receivedCallSign${other.receivedCallSign}")
        if (qso.receivedCallSign != other.sentCallSign) throw new QsoPairException(s"T3: qso.receivedCallSign: ${qso.receivedCallSign} other.sentCallSign: ${other.sentCallSign}")

        println("OK")
      }
    } catch {
      case e:Exception =>
        throw e
    }
  }

  override def qsoPoints()(implicit timeMatcher: TimeMatcher): QsoPoints = {
    score()
  }
  /**
   *
   * @param timeMatcher tells if two [[java.time.Instant]]s are close enough together.
   * @return QsoPoints indicating how many points and why.
   */
  def score()(implicit timeMatcher: TimeMatcher): QsoPoints = {
    if (otherQso.isEmpty) {
      missing // not found
    } else {
      val other = otherQso.get
      if (!timeMatcher(qso.ts, other.ts)) {
        QsoPoints.timeDelta
      } else {
        if (qso.band == other.band &&
          qso.mode == other.mode &&
          qso.sentExchange == other.receivedExchange) {
          QsoPoints.mode(qso.mode)
        }
        else {
          QsoPoints.unMatched
        }
      }
    }
  }

  def display()(implicit timeMatcher: TimeMatcher): String = {
    val cs = otherQso.map(q => q.s.cs).getOrElse("???")
    s"${
      qso.r.cs
    } <=> $cs points:$score()"
  }

  override def mode: String = qso.mode

  override def band: String = qso.band
}



class QsoPairException(message: String) extends Exception(message)
