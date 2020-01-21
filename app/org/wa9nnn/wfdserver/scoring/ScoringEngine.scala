
package org.wa9nnn.wfdserver.scoring

import javax.inject.{Inject, Singleton}
import nl.grons.metrics4.scala.DefaultInstrumented
import org.wa9nnn.wfdserver.model.{LogInstance, StationLog}
@Singleton
class ScoringEngine @Inject()(implicit timeMatcher: TimeMatcher) extends DefaultInstrumented {
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

  /**
   * Official scoring is done after all logs have been submitted. Qsos are compared against worked stations.
   *
   * @param li               as persisted.
   * @param receivedQsoCache matches up with other stations
   * @return
   */
  def official(li: LogInstance, receivedQsoCache: ReceivedQsoCache): ScoringResult = {
    val matchedQsos = li.qsos.map { qso =>
      MatchedQso(qso, receivedQsoCache.apply(qso))
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





