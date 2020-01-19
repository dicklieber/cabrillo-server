
package org.wa9nnn.wfdserver

import org.bson.Document
import org.wa9nnn.wfdserver.htmlTable.{Cell, CellProvider}
import org.wa9nnn.wfdserver.model.WfdTypes.CallSign

/**
 * used to for choices when user need to select a log.
 * @param callSign of log
 * @param logVersion  0-N
 * @param entryId Key
 */
case class CallSignId(callSign: CallSign, logVersion: Int, entryId: String) extends CellProvider with Ordered[CallSignId] {
  override def toCell: Cell = {
    val u = controllers.routes.AdminController.submission(this)
    Cell(s"$callSign:$logVersion")
      .withUrl(u.url)
  }

  override def toString: String = s"$callSign|$logVersion|$entryId"

  override def compare(that: CallSignId): Int = {
    var ret = this.callSign compareTo that.callSign
    if(ret == 0)
      ret = this.logVersion .compareTo(that.logVersion)
    ret
  }
}

object CallSignId {
  private val r = """([^|]+)\|(\d+)\|(.+)""".r

  /**
   * Convert a MongoDB BSON [[Document]] to C=[[CallSignId]].
   * @param doc BSON
   * @return CallSignId
   */
  def apply(doc:Document): CallSignId = {
    val stationLog: Document = doc.get("stationLog").asInstanceOf[Document]
    val callCatSect: Document = stationLog.get("callCatSect").asInstanceOf[Document]
    val cs = callCatSect.getString("callSign")
    val lv = stationLog.getInteger("logVersion")
    new CallSignId(
      callSign = cs,
      logVersion = lv,
      entryId = doc.getString("_id"))
  }

 def apply(callSign: CallSign, logVersion: Int, entryId: Int): CallSignId = {
    new CallSignId(callSign, logVersion, entryId.toString)
  }

  def apply(csi: String): CallSignId = {
    val r(cs, lv, id) = csi
    new CallSignId(cs, lv.toInt, id)
  }
}

