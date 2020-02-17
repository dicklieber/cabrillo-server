
package com.wa9nnn.wfdserver.scoring

import com.wa9nnn.wfdserver.htmlTable._
import com.wa9nnn.wfdserver.model.CallCatSect
import com.wa9nnn.wfdserver.scoring.ScoringResult._

/**
 * Displayable result
 *
 * @param callCatSect     callSign, Category and ARRL Section.
 * @param soapBoxResult   bonus
 * @param qsoResult       details on QSOs
 * @param powerMultiplier based on [[com.wa9nnn.wfdserver.model.StationLog.categories.power]]
 * @param claimedScore    what they though their score was.
 */
case class ScoringResult(callCatSect: CallCatSect, soapBoxResult: SoapBoxesResult, qsoResult: QsoResult, powerMultiplier: Int, claimedScore: Option[Int]) {

  def qsos: Int = qsoResult.qsos

  val withPowerAndBandMode = qsoResult.multipliedPoints * powerMultiplier
  val score: Int = withPowerAndBandMode + soapBoxResult.awardedBonusPoints

  val actualScore: Int = soapBoxResult.awardedBonusPoints
  //      val powerMultiplier = (stationLog.categories.power map {
  //        case "QRP" => 4
  //        case "LOW" => 2
  //        case _ => 1
  //      }).getOrElse(1)


  def table: Table = {

    val grandTotal = Row("Grand", score, "", "Soapbox claims plus multiplied QSO total.")

    val byModeRows: Seq[Row] = qsoResult.byMode.sorted.map(_.toRow)
    val totalsRows: Seq[Row] = byModeRows.appendedAll(Seq(


      AwardTotalsRow("QSO Points", qsoResult.qsoPoints, "Raw qso points before multipliers"),
      MultiplierTotalsRow("Power Multipier", powerMultiplier, powerMultiplierExplain),
      MultiplierTotalsRow("Band/Mode Multiplier", qsoResult.multiplier, qsoResult.multipliersCell)
        .withToolTip("Each combination of mode and band is one multiplier."),
      Row("Multiplied", withPowerAndBandMode, "", "QSO points times multipliers."),
      grandTotal
    )
    )


    val sc: SectionedRowCollector = new SectionedRowCollector()
      .+=("SoapBox/Bonus", SoapBoxAward.columns, soapBoxResult.toRows())
      //      .+=("QSOs", qsoResult.toRows)
      .+=(Cell("Totals"), Seq("Item", "Award", "Multiplier", "Explain"), totalsRows)


    val csc = Seq(Seq(Cell(s"${callCatSect.callSign} Results")
      .withCssClass("sectionHeader")
      .withColSpan(4)))
    Table(csc, sc.rows).withCssClass("resultTable")
  }

  def scoreRecord: ScoreRecord = {
    ScoreRecord(callCatSect, score, claimedScore, qsoResult.errantQsos)
  }
}

object ScoringResult {
  val byModeSection: Seq[Row] => SectionedRows = SectionedRows("By Mode", "Mode", "Award", "Count")

  val powerMultiplierExplain: Cell = TableInACell(Table(Seq(Seq("Multiplier", "Power")), Seq(
    Row("1", "More than 100W"),
    Row("2", "100W or less"),
    Row("4", "QRP")
  )
  ).withCssClass("resultTable")
  )

}


