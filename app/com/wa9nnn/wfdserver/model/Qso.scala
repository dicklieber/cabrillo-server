
package com.wa9nnn.wfdserver.model

import java.time.Instant

import com.wa9nnn.cabrillo.requirements.Frequencies
import com.wa9nnn.wfdserver.htmlTable.{Header, Row, RowSource}
import com.wa9nnn.wfdserver.scoring.QsoBase

/**
 * One QSO record from a Cabrillo file.
 * This can be persisted in MongoDB and the field names take up space so this is using the really tiny field names to help keep mongo data budget down.
 * Nicely named defs are provided to make using this class more sensible.
 * This short names shouldn't leak outside of this class
 *
 * Terminology note: When referring to instance of this it should always use camel-case e.g. ourQso or qso.
 * When referring to a general ham-radio meaning of a QSO, then use the all caps form. e.g. QSO.
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
                r: Exchange) extends RowSource  with QsoBase{


  def sentCallSign: CallSign = s.cs

  def receivedCallSign: CallSign = r.cs

  def mode: String = m

  def band: String = b

  def sentExchange: Exchange = s

  def receivedExchange: Exchange = r

  def toRow: Row = {
    val rr = Row(b, m, s.toCell, r.toCell, ts)
    rr
  }


  override def toString: String = {
    s"$band $mode $s $r"
  }

  def sentKey: QsoKey = {
    QsoKey(sentCallSign = s.cs,
      receivedCallSign = r.cs,
      band = b,
      mode = m
    )
  }

  def receivedKey: QsoKey = {
    QsoKey(sentCallSign = r.cs,
      receivedCallSign = s.cs,
      band = b,
      mode = m
    )
  }

  /**
   * Used to de-duplicate QSO with the same station on the same band and mode.
   * @return a Key
   */
  def dupKey: String = {
    s"$receivedKey|$b|$m"
  }

  /**
   *
   * @return make Qso for other side of actual QSO.
   */
  def swap: Qso = copy(s = r, r = s)


}

object Qso {
  def header(qsoCount: Int): Header = Header(s"QSO ($qsoCount)", "Freq", "Mode", "Sent", "Received", "Stamp")
  def apply(band:String, mode:String, sentExchange:Exchange, receivedExchange:Exchange):Qso = {
    new Qso(band, mode, Instant.now(), sentExchange, receivedExchange)
  }
}

/**
 *
 * Generated from the  [[Qso.sentKey]] to match up with a [[Qso.receivedKey]] at another station.
 *
 * @param sentCallSign sent or received
 * @param receivedCallSign sent or received
 * @param band 20M etc.
 * @param mode DI etc.
 */
case class QsoKey(sentCallSign:CallSign, receivedCallSign: CallSign, band: String, mode: String)
