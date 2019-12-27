
package org.wa9nnn.wfdserver

import org.bson.Document
import org.wa9nnn.wfdserver.htmlTable.{Cell, CellProvider}

/**
 * used to for choices when user need to select a log.
 * @param callsign of log
 * @param logVersion  0-N
 * @param entryId Key
 */
case class CallSignId(callsign: String, logVersion: Int, entryId: String) extends CellProvider {
  override def toCell: Cell = {
    val u = controllers.routes.AdminController.submission(this)
    Cell(s"$callsign:$logVersion")
      .withUrl(u.url)
  }

  override def toString: String = s"$callsign|$logVersion|$entryId"
}

object CallSignId {
  private val r = """([^|]+)\|(\d+)\|(.+)""".r

  /**
   * Convert a MongoDB BSON [[Document]] to C=[[CallSignId]].
   * @param doc BSON
   * @return CallSignId
   */
  def apply(doc:Document): CallSignId = {
    new CallSignId(
      callsign = doc.getString("callSign"),
      logVersion = doc.getInteger("logVersion"),
      entryId = doc.getObjectId("_id").toHexString)
  }

 def apply(callsign: String, logversion: Int, entryId: Int): CallSignId = {
    new CallSignId(callsign, logversion, entryId.toString)
  }

  def apply(csi: String): CallSignId = {
    val r(cs, lv, id) = csi
    new CallSignId(cs, lv.toInt, id)
  }
}

