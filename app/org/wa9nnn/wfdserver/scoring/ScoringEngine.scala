
package org.wa9nnn.wfdserver.scoring

import javax.inject.{Inject, Singleton}
import nl.grons.metrics4.scala.DefaultInstrumented
import org.wa9nnn.wfdserver.model.{LogInstance, Qso, StationLog}
import org.wa9nnn.wfdserver.util.JsonLogging

/**
 * Scores one [[LogInstance]].
 *
 * @param timeMatcher determines if two times are close enough.
 */
@Singleton
class ScoringEngine @Inject()(implicit timeMatcher: TimeMatcher) extends DefaultInstrumented with JsonLogging {
  private val timer = metrics.timer("Score")

  /**
   * Provisional scoring is done at ingestion time. e.i. without de-duping or matching other stations.
   *
   * @param li               as persisted.
   * @return
   */
  def provisional(li: LogInstance): ScoringResult = {
    calcScore(li.stationLog, li.qsos)
  }


  /**
   * Official scoring is done after all logs have been submitted. Qsos are  de-duplicated and compared against worked stations.
   *
   * @param li               as persisted.
   * @param receivedQsoCache matches up with other stations
   * @return
   */
  def official(li: LogInstance, receivedQsoCache: ReceivedQsoCache): ScoringResult = {

    def deDup(qsos: Seq[Qso]) = {
      qsos.groupBy(_.dupKey)
        .map { case (_, matching) =>
          matching.head
        }
    }

    val deDuped = deDup(li.qsos)
    val matchedQsos = deDuped.map { qso =>
      val sentKey = qso.sentKey
      val maybeMatched = receivedQsoCache(sentKey)
      val mqso = MatchedQso(qso, maybeMatched)
      mqso
    }

    calcScore(li.stationLog, matchedQsos)
  }

  /**
   *
   * @param stationLog from a [[LogInstance]]
   * @param qsos       either directly from a [[LogInstance]] or filtered.
   * @return
   */
  private def calcScore(stationLog: StationLog, qsos: Iterable[QsoBase]): ScoringResult = {

    timer.time {

      val qsoAccumulator = new QsoAccumulator

      qsos.foreach(qsoAccumulator(_))

      val powerMultiplier = (stationLog.categories.power map {
        case "QRP" => 4
        case "LOW" => 2
        case _ => 1
      }).getOrElse(1)

      val qsoResult: QsoResult = qsoAccumulator.result

      val soapBoxResult: SoapBoxesResult = SoapBoxParser(stationLog.soapBoxes)

      ScoringResult(stationLog.callCatSect, soapBoxResult, qsoResult, powerMultiplier, stationLog.claimedScore)
    }
  }
}





