
package org.wa9nnn.wfdserver.scoring

import java.time.{Duration, Instant}

import org.wa9nnn.wfdserver.htmlTable.{Header, Row, RowsSource, Table}
import org.wa9nnn.wfdserver.util.CountedThings

/**
 *
 * @param started           when bu;k load started.
 * @param nStations         how files in source directory.
 * @param stationsSoFar     files successfully completed.
 * @param qsoCountSoFar     number of QSOs in those successes.
 * @param failSoFar         number of failures.
 * @param mapSize           number of other stations qsos remaining.
 * @param scoreOneRate
 * @param duration
 * @param qsoKindRows
 * @param finished
 */
case class ScoringStatus(started: Instant = Instant.EPOCH,
                         nStations: Int = 0,
                         stationsSoFar: Int = 0,
                         qsoCountSoFar: Int = 0,
                         failSoFar: Int = 0,
                         mapSize: Int = 0,
                         scoreOneRate: Duration = Duration.ZERO,
                         duration: Option[Duration] = None,
                         qsoKindRows: CountedThings[QsoKind] = CountedThings[QsoKind](Map.empty[QsoKind, Int]),
                         qsoKindTotal: Int = 0,
                         finished: Option[Instant] = None,
                        ) extends RowsSource {
  def finish: ScoringStatus = {
    val doneStamp = Instant.now()
    copy(finished = Some(doneStamp), duration = Some(Duration.between(started, doneStamp)))
  }

  def success(scoreOneResult: ScoreOneResult): ScoringStatus = {
    copy(stationsSoFar = stationsSoFar + 1,
      qsoCountSoFar = qsoCountSoFar + scoreOneResult.scoringResult.qsos,
      scoreOneRate = scoreOneResult.oneMinuteRateDuration,
      mapSize = scoreOneResult.mapSize,
      qsoKindRows = scoreOneResult.qsoKinds,
      qsoKindTotal = scoreOneResult.qsoKinds.size)
  }

  def failure(): ScoringStatus = {
    copy(failSoFar = failSoFar + 1)
  }

  def isRunning: Boolean = {
    started != Instant.EPOCH && finished.isEmpty
  }

  def hasRun: Boolean = started != Instant.EPOCH

  def progress: Int = stationsSoFar + failSoFar

  def table: Table = Table(Header("Bulk Load Status", "Item", "Value"), toRows(includeNone = false))

  override def toRows(includeNone: Boolean, prefix: String): Seq[Row] = {
    super.toRows(includeNone, prefix)
  }
}

