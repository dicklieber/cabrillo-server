
package com.wa9nnn.wfdserver.model

import com.wa9nnn.cabrillo.parsers.Exchange_WFD
import com.wa9nnn.wfdserver.htmlTable.Cell

/**
 * May be sent or received
 * Short variable names to help keep mongo data down.
 *
 * @param cs callSign
 * @param ex exchange e.g. "3O IL"
 */
case class Exchange(cs: CallSign, ex: String) {
  def toCell: Cell = Cell(s"${cs.toString.padTo(7, ' ')} $ex").withCssClass("exchange")

  def categoryAndSection: (String, String) = {
    val tokens = ex.split(" ")
    tokens(0) -> tokens(1)
  }

  override def toString: String = {
    s"[$cs $ex]"
  }
}

object Exchange {
  def apply(callSign: CallSign, category: String, section: String): Exchange = {
    new Exchange(callSign, s"$category $section")
  }

  def apply(callCatSect: CallCatSect): Exchange = {
    apply(callCatSect.callSign, callCatSect.category, callCatSect.arrlSection)
  }

  def apply(e: Exchange_WFD): Exchange = {
    apply(e.callsign, e.category, e.section)
  }

}
