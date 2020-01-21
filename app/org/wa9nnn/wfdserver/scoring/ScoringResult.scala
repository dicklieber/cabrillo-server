
package org.wa9nnn.wfdserver.scoring

import controllers.ScoreRecord
import org.wa9nnn.wfdserver.htmlTable.{Cell, SectionedRowCollector, Table}
import org.wa9nnn.wfdserver.model.CallCatSect

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

