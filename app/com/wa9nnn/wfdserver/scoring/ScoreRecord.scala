
package com.wa9nnn.wfdserver.scoring

import com.wa9nnn.wfdserver.db.ScoreFilter
import com.wa9nnn.wfdserver.htmlTable.{Cell, Row}
import com.wa9nnn.wfdserver.model.CallCatSect
import controllers.routes

import scala.language.implicitConversions

case class ScoreRecord(callCatSect: CallCatSect, awardedPoints: Int, claimedPoints: Option[Int], errant: Seq[MatchedQso], overallRank: Option[Int] = None, categoryRank: Option[Int] = None) {
  def toRow(scoreFilter: ScoreFilter): Row = {
    val pointsClass = if (claimedPoints.getOrElse(-1) == awardedPoints) {
      "pointsMatch"
    } else {
      "pointsDontMatch"
    }
    Row(
      Cell(callCatSect.callSign)
        .withUrl(routes.AdminController.submission(callCatSect.callSign.toString).url),
      callCatSect.category,
      callCatSect.arrlSection,
      overallRank,
      categoryRank,
      Cell(awardedPoints).withCssClass(pointsClass),
      Cell(claimedPoints.getOrElse("")).withCssClass(pointsClass),
      if (scoreFilter.includeErrantDetail) {
        Cell.rawHtml(errant.map(_.toString).mkString("<br/>")).withCssClass("matchedQsos")
      } else {
        if (errant.isEmpty) {
          ""
        } else {
          f"${errant.size} bad QSOs"
        }
      }
    )
  }

  def toCsv: Array[String] = {
    implicit def iToS(in: Int): String = in.toString
    implicit def oiToS(in: Option[Int]): String = in.map(in => in.toString).getOrElse("")

    val ccs = callCatSect
    Seq[String](ccs.callSign.toString, ccs.category, ccs.arrlSection, overallRank, categoryRank, awardedPoints, claimedPoints, errant.length).toArray
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