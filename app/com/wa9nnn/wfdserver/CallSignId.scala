
package com.wa9nnn.wfdserver

import org.bson.Document
import com.wa9nnn.wfdserver.htmlTable.{Cell, CellProvider}
import com.wa9nnn.wfdserver.model.WfdTypes.CallSign

/**
 * used to for choices when user need to select a log.
 * @param callSign of log
 * @param entryId Key
 */
case class CallSignId(callSign: CallSign, entryId: String) extends CellProvider with Ordered[CallSignId] {
  override def toCell: Cell = {
    val u = controllers.routes.AdminController.submission(entryId)
    Cell(s"$callSign")
      .withUrl(u.url)
  }

  override def toString: String = s"$callSign|$entryId"

  override def compare(that: CallSignId): Int = {
   this.callSign compareTo that.callSign
  }
}

object CallSignId {
  /**
   * Convert a MongoDB BSON [[Document]] to C=[[CallSignId]].
   * @param doc BSON
   * @return CallSignId
   */
  def apply(doc:Document): CallSignId = {
    new CallSignId(callSign = doc.getString("_id"), entryId = doc.getString("_id"))
  }

 def apply(callSign: CallSign, entryId: Int): CallSignId = {
    new CallSignId(callSign, entryId.toString)
  }

}

