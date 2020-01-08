
package org.wa9nnn.wfdserver.scoring

import org.wa9nnn.wfdserver.htmlTable.{Cell, SectionedRowCollector, Table}
import org.wa9nnn.wfdserver.model.LogInstance

object ScoringEngine {
  def apply(li: LogInstance): ScoringResult = {

    val qsoAccumulator = new QsoAccumulator
    li.qsos.foreach(qsoAccumulator(_))
    val powerMultiplier = (li.stationLog.categories.power map {
      case "QRP" => 4
      case "LOW" => 2
      case _ => 1
    }).getOrElse(1)

    val qsoResult: QsoResult = qsoAccumulator.result(powerMultiplier)

    val soapBoxResult: SoapBoxesResult = SoapBoxParser(li.stationLog.soapBoxes)

    ScoringResult(li.stationLog.callSign, soapBoxResult, qsoResult)
  }
}

case class ScoringResult(callSign: String, soapBoxResult: SoapBoxesResult, qsoResult: QsoResult) {

  def table: Table = {

    val sc: SectionedRowCollector = (new SectionedRowCollector()
      .+=("SoapBox/Bonus", SoapBoxAward.columns, soapBoxResult.toRows())
      .+=("QSOs", qsoResult.toRows(soapBoxResult.awardedBonusPoints)))


    val csc = Seq(Seq(Cell(s"$callSign Results")
      .withCssClass("sectionHeader")
      .withColSpan(4)))
    Table(csc, sc.rows).withCssClass("resultTable")
  }
}



