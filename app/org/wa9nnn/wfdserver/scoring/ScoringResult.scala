
package org.wa9nnn.wfdserver.scoring

import org.wa9nnn.wfdserver.htmlTable._
import org.wa9nnn.wfdserver.model.CallCatSect
import org.wa9nnn.wfdserver.scoring.ScoringResult._

/**
 *
 * @param callCatSect callSign, Category and ARRL Section.
 * @param soapBoxResult bonus
 * @param qsoResult
 * @param score
 */
case class ScoringResult(callCatSect: CallCatSect,  soapBoxResult: SoapBoxesResult, qsoResult: QsoResult, score:Int) {

  def qsos:Int = qsoResult.qsos
  def table: Table = {

    val sc: SectionedRowCollector = new SectionedRowCollector()
      .+=("SoapBox/Bonus", SoapBoxAward.columns, soapBoxResult.toRows())
      .+=("QSOs", qsoResult.toRows(soapBoxResult.awardedBonusPoints))


    val csc = Seq(Seq(Cell(s"${callCatSect.callSign} Results")
      .withCssClass("sectionHeader")
      .withColSpan(4)))
    Table(csc, sc.rows).withCssClass("resultTable")
  }

  def scoreRecord:ScoreRecord = {
    ScoreRecord(callCatSect, score)
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


