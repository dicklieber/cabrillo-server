
package org.wa9nnn.wfdserver.scoring

import javax.inject.{Inject, Singleton}
import nl.grons.metrics4.scala.DefaultInstrumented
import org.wa9nnn.wfdserver.model.{LogInstance, StationLog}
import org.wa9nnn.wfdserver.util.JsonLogging

@Singleton
class ScoringEngine @Inject()(implicit timeMatcher: TimeMatcher) extends DefaultInstrumented with JsonLogging {
  private val timer = metrics.timer("Score")

  /**
   * Provisional scoring is done at ingestion time. e.i. without matching to other stations.
   *
   * @param li               as persisted.
   * @return
   */
  def provisional(li: LogInstance): ScoringResult = {
    calcScore(li.stationLog, li.qsos)
  }

  var soFar: Int = 0

  /**
   * Official scoring is done after all logs have been submitted. Qsos are compared against worked stations.
   *
   * @param li               as persisted.
   * @param receivedQsoCache matches up with other stations
   * @return
   */
  def official(li: LogInstance, receivedQsoCache: ReceivedQsoCache): ScoringResult = {
    soFar += 1
    val matchedQsos = li.qsos.map { qso =>
      val sentKey = qso.sentKey
      val maybeMatched = receivedQsoCache(sentKey)
      val mqso = MatchedQso(qso, maybeMatched)
      if (mqso.otherQso.isEmpty) {
        //        val receivedCallSign = mqso.qso.receivedCallSign
        //        val receivedKey = mqso.qso.receivedKey
        //        val rk = receivedQsoCache.map.get(receivedKey)
        //        val sk = receivedQsoCache.map.get(sentKey)

        //        logger.debug(s"Unmatched: ${mqso.qso}")

      }
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
  private def calcScore(stationLog: StationLog, qsos: Seq[QsoBase]): ScoringResult = {

    timer.time {
      val qsoAccumulator = new QsoAccumulator

      qsos.foreach(qsoAccumulator(_))

      val powerMultiplier = (stationLog.categories.power map {
        case "QRP" => 4
        case "LOW" => 2
        case _ => 1
      }).getOrElse(1)

      val qsoResult: QsoResult = qsoAccumulator.result(powerMultiplier)

      val soapBoxResult: SoapBoxesResult = SoapBoxParser(stationLog.soapBoxes)

      //      val score = -42
      ScoringResult(stationLog.callCatSect, soapBoxResult, qsoResult, qsoResult.qsoPoints)
    }
  }
}





