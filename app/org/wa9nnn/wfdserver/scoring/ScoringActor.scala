
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
  /**
   * This is the only piece of mutable state. It is updates from message from the child [[ScoringTask]] actor
   * and may be queried, typically be a Controller.
   */
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


case class StartScoringRequest(wfdSubject: WfdSubject)