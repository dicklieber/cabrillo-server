
package org.wa9nnn.wfdserver.model

import java.time.Instant

import org.wa9nnn.cabrillo.requirements.Frequencies
import org.wa9nnn.wfdserver.htmlTable.{Header, Row, RowSource}
import org.wa9nnn.wfdserver.model.WfdTypes.CallSign
import org.wa9nnn.wfdserver.scoring.{QsOPointer, QsoPoints}

/**
 * This can be persisted in MongoDB and the field names take up space so this is using the really tiny field names to help keep mongo data budget down.
 * Nicely named defs are provided to make using this class more sensible.
 * This short names shouldn't leak outside of this class
 *
 * @param b  band as defined in [[Frequencies]]
 * @param m  mode
 * @param ts timestamp
 * @param s  sent
 * @param r  received
 */
case class Qso(
                b: String,
                m: String,
                ts: Instant,
                s: Exchange,
                r: Exchange) extends RowSource {
  def toRow: Row = {
    val rr = Row(b, m, s.toCell, r.toCell, ts)
    rr
  }

  def sentKey: QsoKey = {
    QsoKey(callSign = s.cs,
      band = b,
      mode = m
    )
  }

  def receivedKey: QsoKey = {
    QsoKey(callSign = r.cs,
      band = b,
      mode = m
    )
  }

  override val points: Int = QsoPoints.mode(mode).points
}



object Qso {
  def header(qsoCount: Int): Header = Header(s"QSO ($qsoCount)", "Freq", "Mode", "Sent", "Received", "Stamp")
}

