
package org.wa9nnn.wfdserver.model

import java.time.Instant

import org.wa9nnn.cabrillo.requirements.Frequencies
import org.wa9nnn.wfdserver.htmlTable.{Header, Row, RowSource}
import org.wa9nnn.wfdserver.scoring.ModeBand

/**
 * This can be persisted in MongoDB and the field names take up space so this is using the really tiny field names to help keep mongo data budget down.
 *
 * @param b  band as defined in [[Frequencies]]
 * @param m  mode
 * @param ts timestamo
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

  lazy val modeBand: ModeBand = ModeBand(m, Frequencies.check(b))
}



object Qso {
  def header(qsoCoount: Int): Header = Header(s"QSO ($qsoCoount)", "Freq", "Mode", "Sent", "Received", "Stamp")
}

