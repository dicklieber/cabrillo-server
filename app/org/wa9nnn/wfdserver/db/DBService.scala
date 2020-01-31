
package org.wa9nnn.wfdserver.db

import org.wa9nnn.wfdserver.CallSignId
import org.wa9nnn.wfdserver.auth.WfdSubject
import org.wa9nnn.wfdserver.htmlTable.Table
import org.wa9nnn.wfdserver.model.LogInstance
import org.wa9nnn.wfdserver.model.WfdTypes.CallSign
import org.wa9nnn.wfdserver.scoring.ScoreRecord

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
  def callSignIds()(implicit subject: WfdSubject): Future[Seq[CallSignId]]

  /**
   * ignores case
   *
   * @param partialCallSign to search for.
   * @return
   */
  def search(partialCallSign: String)(implicit subject: WfdSubject): Future[Seq[CallSignId]]

  def recent()(implicit subject: WfdSubject): Future[Seq[CallSignId]]

  /**
   *
   * @param entryId as returned in a [[CallSignId]] from [[callSignIds]].
   * @return the LogInstance for the entryId or None.
   */
  def logInstance(entryId: String)(implicit subject: WfdSubject): Future[Option[LogInstance]]


  def stationCount()(implicit subject: WfdSubject): Int

  /**
   * Actual stats depend on the database used.
   *
   * @return
   */
  def stats()(implicit subject: WfdSubject): Future[Table]

  /*
  Scoring use a different database than the other APIs.
   */
  def dropScoringDb()(implicit subject: WfdSubject): Unit

  def putScore(scoreRecord: ScoreRecord)(implicit subject: WfdSubject): Unit

  def getScores()(implicit subject: WfdSubject):Future[Seq[ScoreRecord]]

  protected val recentLimit: Int = 25
}


