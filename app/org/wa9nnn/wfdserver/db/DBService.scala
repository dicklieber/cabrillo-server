
package org.wa9nnn.wfdserver.db

import controllers.ScoreRecord
import org.wa9nnn.wfdserver.CallSignId
import org.wa9nnn.wfdserver.auth.WfdSubject
import org.wa9nnn.wfdserver.htmlTable.Table
import org.wa9nnn.wfdserver.model.LogInstance
import org.wa9nnn.wfdserver.model.WfdTypes.CallSign

import scala.concurrent.Future

/**
 * Things that can be done by MongoDB or MySQL.
 */
trait DBService {
  def getLatest(callSign: CallSign)(implicit subject: WfdSubject): Future[Option[LogInstance]]

  /**
   *
   * @param logInstance in coming.
   * @return LogInstance with logVersion.
   */
  def ingest(logInstance: LogInstance): LogInstance

  /**
   *
   * @return all entries.
   */
  def callSignIds: Future[Seq[CallSignId]]

  /**
   * ignores case
   *
   * @param partialCallSign to search for.
   * @return
   */
  def search(partialCallSign: String): Future[Seq[CallSignId]]

  def recent: Future[Seq[CallSignId]]

  /**
   *
   * @param entryId as returned in a [[CallSignId]] from [[callSignIds]].
   * @return the LogInstance for the entryId or None.
   */
  def logInstance(entryId: String): Future[Option[LogInstance]]

  /**
   * Actual stats depend on the database used.
   *
   * @return
   */
  def stats: Future[Table]

  protected val recentLimit: Int = 25
}


