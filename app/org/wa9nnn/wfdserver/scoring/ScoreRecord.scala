
package org.wa9nnn.wfdserver.scoring

import controllers.routes
import org.wa9nnn.wfdserver.htmlTable.{Cell, Header, Row, RowSource}
import org.wa9nnn.wfdserver.model.CallCatSect

case class ScoreRecord(callCatSect: CallCatSect, points:Int, errant: Seq[MatchedQso])  extends RowSource {
  override def toRow: Row = {
    Row(
      Cell(callCatSect.callSign)
      .withUrl(routes.AdminController.submission(callCatSect.callSign).url),
      callCatSect.category,
      callCatSect.arrlSection,
      points,
      Cell.rawHtml(errant.map(_.toString).mkString("<br/>")).withCssClass("matchedQsos")
    )
  }
}

object ScoreRecord {
  val header:Header = Header("Station Scores", "CallSign", "Category", "Section", "Score", "Errant QSOs")
}