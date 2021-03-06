
package com.wa9nnn.wfdserver.scoring

import java.time.Instant

import akka.actor.Actor
import javax.inject.Inject
import nl.grons.metrics4.scala.DefaultInstrumented
import com.wa9nnn.wfdserver.CallSignId
import com.wa9nnn.wfdserver.db.DBRouter
import com.wa9nnn.wfdserver.htmlTable.Row
import com.wa9nnn.wfdserver.model.LogInstance
import com.wa9nnn.wfdserver.util.{Counted, CountedThings, JsonLogging}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class ScoringTask @Inject()(db: DBRouter, scoringEngine: ScoringEngine, receivedQsoCache: ReceivedQsoCache) extends Actor with DefaultInstrumented with JsonLogging {
  private val stationMeter = metrics.meter("StationMeter")
  private val scoreOneTimer = metrics.timer("scoreOne")
  private var started: Option[Instant] = None
  private var stationsToDo = 0
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  private val qsoKinds = new Counted[QsoKind]

  def start(ssr: StartScoringRequest): Unit = {
    implicit val subject = ssr.wfdSubject
    started = Option(Instant.now)
    val parent = context.parent
    metrics.timer("ScoringTimer").time {
      val count = db.stationCount()
      val recordBuilder = Seq.newBuilder[ScoreRecord]

      stationsToDo = count
      parent ! StartingScoring(stationsToDo)
      logJson("scoring Start").++("stationsToDo" -> stationsToDo)
      db.dropScoringDb()

      val callSignIds = Await.result[Seq[CallSignId]](db.callSignIds(), 1 minute)
      callSignIds.foreach { csid =>
        try {
          val result = scoreOneTimer.time {
            val logInstance: Option[LogInstance] = Await.result[Option[LogInstance]](db.logInstance(csid.entryId), 30 seconds)
            logInstance.map(logInstance => {
              val scoringResult: ScoringResult = scoringEngine.official(logInstance, receivedQsoCache)
              //              db.putScore(scoringResult.scoreRecord)
              recordBuilder += scoringResult.scoreRecord
              stationMeter.mark()
              qsoKinds(scoringResult.qsoResult.qsoKinds)
              scoringResult
            }
            )


          }
          try {
            val duration = java.time.Duration.ofNanos(scoreOneTimer.mean.toLong)
            parent ! ScoreOneResult(result.get, duration, receivedQsoCache.mapSize, qsoKinds.result)
          } catch {
            case e: Exception =>
              logger.error("calc duration", e)
          }

        } catch {
          case e: Exception =>
            logger.error(s"Station: ${csid.callSign}", e)
        }
      }
      val ranked = Ranking(recordBuilder.result())
      db.putScores(ranked)
    }

    receivedQsoCache.mapValues.foreach { qso =>
      logger.error(s"leftOver: $qso")
    }
    val mapSize = receivedQsoCache.mapSize
    logJson("scoring finish")
      .++("stationsToDo" -> stationsToDo)
      .++("mapSize" -> mapSize)
      .info()

  }

  override def receive: Receive = {
    case ssr: StartScoringRequest =>
      try {
        started
          .map(_ => logger.error(s"Scoring already running since $started."))
          .orElse {
            start(ssr)
            context.parent ! ScoringDone
            context.stop(self)
            None
          }
      } catch {
        case e: Exception =>
          logger.error(s"Top-level scoring exception.")
      }

    case x =>
      logger.error(s"Unexpected message: $x")
  }
}


case class ScoreOneResult(scoringResult: ScoringResult, oneMinuteRateDuration: java.time.Duration, mapSize: Int, qsoKinds: CountedThings[QsoKind])

object ScoringTask {
  var scoringTaskName = "scoringTask"
}
