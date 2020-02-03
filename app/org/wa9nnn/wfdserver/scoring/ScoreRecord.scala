
package org.wa9nnn.wfdserver.scoring

import controllers.routes
import org.wa9nnn.wfdserver.htmlTable.{Cell, Row, RowSource}
import org.wa9nnn.wfdserver.model.CallCatSect

case class ScoreRecord(callCatSect: CallCatSect, awardedPoints: Int, claimedPoints: Option[Int], errant: Seq[MatchedQso], overallRank: Option[Int] = None, categoryRank: Option[Int] = None)
  extends RowSource {
  override def toRow: Row = {
    val pointsClass = if (claimedPoints.getOrElse(-1) == awardedPoints) {
      "pointsMatch"
    } else {
      "pointsDontMatch"
    }
    Row(
      Cell(callCatSect.callSign)
        .withUrl(routes.AdminController.submission(callCatSect.callSign).url),
      callCatSect.category,
      callCatSect.arrlSection,
      overallRank,
      categoryRank,
      Cell(awardedPoints).withCssClass(pointsClass),
      Cell(claimedPoints.getOrElse("")).withCssClass(pointsClass),
      Cell.rawHtml(errant.map(_.toString).mkString("<br/>")).withCssClass("matchedQsos")
    )
  }
}

object ScoreRecord {
  def header(stationCount: Int): Seq[Seq[Any]] = Seq(
    Seq(Cell(f"Station Scores ($stationCount)")
      .withColSpan(8)
      .withCssClass("tablesorter-noSort")),
    Seq(
      Cell("CallSign").withRowSpan(2),
      Cell("Category").withRowSpan(2),
      Cell("Section").withRowSpan(2),
      Cell("Rank")
        .withColSpan(2)
        .withCssClass("tablesorter-noSort"),
      Cell("Score")
        .withColSpan(2)
        .withCssClass("tablesorter-noSort"),
      Cell("Errant QSOs").withRowSpan(2)
    ),
    Seq(Cell("Overall"),
      Cell("Category"),
      Cell("Awarded"),
      Cell("Claimed")
    )
  )
}