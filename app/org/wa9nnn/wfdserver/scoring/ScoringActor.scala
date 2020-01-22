
package org.wa9nnn.wfdserver.scoring

import java.time.{Duration, Instant}

import akka.actor.Actor
import nl.grons.metrics4.scala.DefaultInstrumented
import org.wa9nnn.wfdserver.actor.GuiceActorCreator
import org.wa9nnn.wfdserver.auth.WfdSubject
import org.wa9nnn.wfdserver.bulkloader.{FailedOne, StatusRequest}
import org.wa9nnn.wfdserver.htmlTable.{Header, Row, RowsSource, Table}
import org.wa9nnn.wfdserver.scoring.ScoringActor._
import org.wa9nnn.wfdserver.util.{Counted, JsonLogging}

/**
 * Actual work is delegated to [[ScoringTask]] actor.
 */
class ScoringActor(guiceActorCreator: GuiceActorCreator) extends Actor with DefaultInstrumented with JsonLogging {

  private var scoringStatus: ScoringStatus = ScoringStatus()

  override def receive: Receive = {
    case ssr: StartScoringRequest =>
      context.child(taskActorName).map(_ =>
        logger.error(s"Scoring task already running!"))
        .orElse {
          start(ssr)
          scoringStatus = ScoringStatus(started = Instant.now())
          None
        }

    case ss: StartingScoring =>
      scoringStatus = ScoringStatus(started = Instant.now(), nStations = ss.nStations)

    case sor: ScoreOneResult =>
      scoringStatus = scoringStatus.success(sor)
    case FailedOne =>
      scoringStatus = scoringStatus.failure()

    case StatusRequest =>
      sender ! scoringStatus
    case ScoringDone =>
      if (scoringStatus.isRunning) {
        scoringStatus = scoringStatus.finish
      } else {
        logger.error(s"ScoringDone while not running!")
      }
    case x =>
      logger.error(s"unexpected message: $x")
  }

  private def start(startScoringRequest: StartScoringRequest): Unit = {

    val scoringTask = context.actorOf(guiceActorCreator[ScoringTask]())

    scoringTask ! startScoringRequest
  }
}

object ScoringActor {
  val taskActorName: String = "scoringTask"
}


case class StartingScoring(nStations: Int)

case object ScoringDone

/**
 *
 * @param started          when bu;k load started.
 * @param nStations        how files in source directory.
 * @param stationsSoFar     files successfully completed.
 * @param qsoCountSoFar    number of QSOs in those successes.
 * @param failSoFar        number of failures.
 */
case class ScoringStatus(started: Instant = Instant.EPOCH,
                         nStations: Int = 0,
                         stationsSoFar: Int = 0,
                         qsoCountSoFar: Int = 0,
                         failSoFar: Int = 0,
                         mapSize:Int = 0,
                         scoreOneRate: Duration = Duration.ZERO,
                         duration: Option[Duration] = None,
                         qsoKindRows:Counted[QsoKind] = new Counted[QsoKind],
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
      qsoKindRows = scoreOneResult.qsoKinds)
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

case class StartScoringRequest(wfdSubject: WfdSubject)