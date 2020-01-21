
package controllers

import org.wa9nnn.wfdserver.htmlTable.{Header, Row, RowSource}
import org.wa9nnn.wfdserver.model.CallCatSect

case class ScoreRecord(callCatSect: CallCatSect, points:Int)  extends RowSource {
  override def toRow: Row = {
    Row(
      callCatSect.callSign,
      callCatSect.category,
      callCatSect.arrlSection,
      points
    )
  }
}

object ScoreRecord {
  val header:Header = Header("Station Scores", "CallSign", "Category", "Section", "Score")
}