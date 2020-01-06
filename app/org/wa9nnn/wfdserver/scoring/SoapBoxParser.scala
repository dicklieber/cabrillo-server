
package org.wa9nnn.wfdserver.scoring

import java.text.NumberFormat
import java.util.Locale

import org.wa9nnn.wfdserver.htmlTable.{Cell, Header, Row, RowSource, RowsSource}

import scala.util.matching.Regex


/**
 * Attempt to examine a SoapBox Body to determine which bonus is being claimed
 */
object SoapBoxParser {

  def apply(soapBoxes:List[String]) :SoapBoxesResult = {
    SoapBoxesResult( soapBoxes.map(SoapBoxParser(_)))
  }

  def apply(soapBoxBody: String): SoapBoxAward = {
    try {
      val r(sClaimedPoints) = soapBoxBody
      val requestedPoints: Int = numberFormat.parse(sClaimedPoints).intValue()
      claimers.flatMap(soapBoxClaimer =>
        soapBoxClaimer(soapBoxBody, requestedPoints)).headOption
        .getOrElse(SoapBoxAward("??", soapBoxBody, Some("Can't parse line"), 0, 0))
    } catch {
      case _: MatchError =>
        SoapBoxAward("??", soapBoxBody, Some("No parse!"), 0, 0)
      case e: Exception =>
        SoapBoxAward("??", soapBoxBody, Some(e.getMessage), 0, 0)
    }
  }

  // were assumed the number being claimed comes first, followed the reason text.
  private val r: Regex = """([,\d]+)\s*.*""".r

  private val numberFormat: NumberFormat = NumberFormat.getNumberInstance(Locale.US) // allows common separator.
  private val claimers: Seq[SoapBoxClaimer] = Seq(
    SoapBoxClaimer("power", "commercial", "power"),
    SoapBoxClaimer("outdoors", "outdoor"),
    SoapBoxClaimer("home", "home"),
    SoapBoxClaimer("satellite", "satellite"),
  )
  val claimNames: Set[String] = claimers.map(_.name).toSet

  /**
   * looks for a word in the reason to determine which award.
   *
   * @param name  of the claim
   * @param words any one of which indicate that this is being claimed for.
   */
  case class SoapBoxClaimer(name: String, words: String*) {
    /**
     *
     * @param body with out tag name.
     * @param claimedPoints as parsed/
     * @return Some if we able to determine match.
     */
    def apply(body: String, claimedPoints: Int): Option[SoapBoxAward] = {
      words.find(body
        .toLowerCase
        .contains(_))
        .map(_ => SoapBoxAward(name, body, None, claimedPoints))
//        .getOrElse(SoapBoxAward("??", body, Some("Can't parse line"), 0, 0))
    }
  }

}

/**
 *
 * @param name          of claim. One of the values in [[SoapBoxParser.claimNames]]
 * @param body          what was considered
 * @param error         Some if could not handle, otherwise None if OK.
 * @param claimedPoints how many they claimed.
 * @param awardedPoints how many are actually awarded.
 */
case class SoapBoxAward(name: String, body: String = "test", error: Option[String] = None, claimedPoints: Int = 1500, awardedPoints: Int = 1500) extends RowSource {
  def claimedEqAwarded: Boolean = claimedPoints == awardedPoints

  def cssClass(row: Row): Row = {
    error match {
      case Some(_) =>
        row.withCssClass("errorRow")
      case None =>
        row
    }
  }

  override def toRow: Row = {
    val row = Row(name, awardedPoints, error.getOrElse("ok"), body)
    cssClass(row)
  }
}

object SoapBoxAward {
  val columns:Seq[String] = Seq("Type", "Awarded", "Status",  "SoapBox")
}


case class SoapBoxesResult(soapBoxAwards: List[SoapBoxAward]) extends RowsSource{
  lazy val awardedBonusPoints:Int = soapBoxAwards.foldLeft(0){ case (accum, sba) =>
    accum + sba.awardedPoints
  }

  override def toRows(includeNone: Boolean, prefix: String): Seq[Row] = {
    soapBoxAwards.map(_.toRow)
  }
}

object SoapBoxesResult {
  val sectionHeader:Row = Row(Seq(Cell("SoapBox/Bonus").withCssClass("sectionHeader")))
}
