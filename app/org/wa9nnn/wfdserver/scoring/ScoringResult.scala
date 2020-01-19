
package org.wa9nnn.wfdserver.scoring

import org.wa9nnn.wfdserver.htmlTable.{Cell, SectionedRowCollector, Table}

case class ScoringResult(callSign: String, soapBoxResult: SoapBoxesResult, qsoResult: QsoResult) {

  def table: Table = {

    val sc: SectionedRowCollector = new SectionedRowCollector()
      .+=("SoapBox/Bonus", SoapBoxAward.columns, soapBoxResult.toRows())
      .+=("QSOs", qsoResult.toRows(soapBoxResult.awardedBonusPoints))


    val csc = Seq(Seq(Cell(s"$callSign Results")
      .withCssClass("sectionHeader")
      .withColSpan(4)))
    Table(csc, sc.rows).withCssClass("resultTable")
  }
}
