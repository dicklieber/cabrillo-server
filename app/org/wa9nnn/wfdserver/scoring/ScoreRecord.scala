
package org.wa9nnn.wfdserver.scoring

import controllers.routes
import org.wa9nnn.wfdserver.htmlTable.{Cell, Row, RowSource}
import org.wa9nnn.wfdserver.model.CallCatSect

case class ScoreRecord(callCatSect: CallCatSect, awardedPoints: Int, claimedPoints: Option[Int], errant: Seq[MatchedQso]) extends RowSource {
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
      Cell(awardedPoints).withCssClass(pointsClass),
      Cell(claimedPoints.getOrElse("")).withCssClass(pointsClass),
      Cell.rawHtml(errant.map(_.toString).mkString("<br/>")).withCssClass("matchedQsos")
    )
  }
}

object ScoreRecord {
  def header(stationCount: Int): Seq[Seq[Any]] = Seq(
    Seq(Cell(f"Station Scores ($stationCount)")
      .withColSpan(6)),
    Seq(
      Cell("CallSign").withRowSpan(2),
      Cell("Category").withRowSpan(2),
      Cell("Section").withRowSpan(2),
      Cell("Score").withColSpan(2),
      Cell("Errant QSOs").withRowSpan(2)
    ),
    Seq("Awaded", "Claimed")
  )
}