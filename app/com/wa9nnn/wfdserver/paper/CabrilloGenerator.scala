
package com.wa9nnn.wfdserver.paper

import java.io.{PrintWriter, StringWriter}

import com.wa9nnn.cabrillo.model.CabrilloData
import com.wa9nnn.cabrillo.parsers.Exchange_WFD
import com.wa9nnn.cabrillo.{Builder, CabrilloWriter, QSO_WFDBuilder}
import com.wa9nnn.wfdserver.db.LogInstanceAdapter
import com.wa9nnn.wfdserver.scoring.ScoringEngine
import javax.inject.{Inject, Singleton}

import scala.util.Using

@Singleton
class CabrilloGenerator @Inject()(scoringEngine: ScoringEngine) {
  def string(paperLog: PaperLog): String = {
    val data = toCabrilloData(paperLog)
    Using.Manager { use =>
      val sw = use(new StringWriter())
      val pw = use(new PrintWriter(sw))

      CabrilloWriter.write(data, pw)
      pw.flush()
      sw.toString
    }.get
  }

  def toCabrilloData(paperLog: PaperLog): CabrilloData = {
    val instance = LogInstanceAdapter(build(paperLog))
    val result = scoringEngine.provisional(instance)
    val score = result.withPowerAndBandMode

    build(paperLog, score)
  }

  def build(paperLog: PaperLog, claimedScore: Int = 0) = {
    implicit def int2Str(n: Int): String = n.toString
    val builder = new Builder

    val h: PaperLogHeader = paperLog.paperLogHeader
    builder.+("CREATED-BY", s"WFD Paper Log by ${paperLog.paperLogDetails.user}")
    builder.+("CONTEST", "WFD")
    builder.+("CALLSIGN", h.callSign.cs)
    builder.+("LOCATION", h.section)
    builder.+("ARRL-SECTION", h.section)
    builder.+("CATEGORY", h.category)
    builder.+("CATEGORY-POWER", h.txPower.toString)
    if (h.noMainPower)
      builder.+("SOAPBOX", "1,500 points for not using commercial power.")
    if (h.outdoors)
      builder.+("SOAPBOX", "1,500 points for setting up outdoors.")
    if (h.awayFromHome)
      builder.+("SOAPBOX", "1,500 points for setting up away from home.")
    if (h.satellite)
      builder.+("SOAPBOX", "1,500 points for satellite QSO(s).")
    builder.+("ADDRESS", h.address)
    builder.+("ADDRESS-CITY", h.city)
    builder.+("ADDRESS-STATE-PROVINCE", h.stateProvince)
    builder.+("ADDRESS-POSTALCODE", h.postalCode)
    builder.+("ADDRESS-COUNTRY", h.country)
    builder.+("EMAIL", h.email)
    builder.+("CLAIMED-SCORE", claimedScore)
    paperLog.qsos.zipWithIndex.foreach { case (qso, index) =>
      val qBuilder = (new QSO_WFDBuilder).freq(qso.freq)
        .mode(qso.mode)
        .stamp(qso.instant)
        .sent(Exchange_WFD(paperLog.callSign.cs, h.category, h.section))
        .received(Exchange_WFD(qso.theirCall.cs, qso.category, qso.section))
      val qsowfd = qBuilder.result(index)
      builder.+(qsowfd)
    }
    builder.toCabrilloData
  }
}
