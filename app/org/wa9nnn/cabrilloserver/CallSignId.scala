
package org.wa9nnn.cabrilloserver

import org.wa9nnn.cabrilloserver.htmlTable.{Cell, CellProvider}


case class CallSignId(callsign: String, logversion: Int, entryId: Int) extends CellProvider {
  override def toCell: Cell = {
    val u = controllers.routes.AdminController.submission(this)
    Cell(s"$callsign:$logversion")
      .withUrl(u.url)
  }

  override def toString: String = s"$callsign|$logversion|$entryId"
}

object CallSignId {
  private val r = """([^|]+)\|(\d+)\|(\d+)""".r

  def apply(csi: String): CallSignId = {
    val r(cs, lv, id) = csi
    new CallSignId(cs, lv.toInt, id.toInt)
  }
}

