
package com.wa9nnn.wfdserver.scoring

import java.util.concurrent.atomic.AtomicInteger

import com.wa9nnn.wfdserver.util.Counted

import scala.collection.concurrent.TrieMap

class QsoAccumulator(implicit timeMatcher: TimeMatcher) {
  private val modeBands = Set.newBuilder[ModeBand]
  private val modeCountMap = new TrieMap[String, AtomicInteger]()
  private val bandCountMap = new TrieMap[String, AtomicInteger]()
  private var qsoScore: Int = 0
  private val errantQsosBuilder = Seq.newBuilder[MatchedQso]
  private var qsos: Seq[QsoBase] = Seq.empty
  private val qsoKinds = new Counted[QsoKind]


  def apply(qso: QsoBase): Unit = {
    val qsoKind: QsoKind = qso.score()
    qsoKinds(qsoKind)
    if (qsoKind.isErrant) {
      errantQsosBuilder += qso.asInstanceOf[MatchedQso]
    }
    qsoScore += qsoKind.points
    val modeBand = qso.modeBand
    modeBands += modeBand
    modeCountMap.getOrElseUpdate(qso.mode, new AtomicInteger()).incrementAndGet()
    bandCountMap.getOrElseUpdate(qso.band, new AtomicInteger()).incrementAndGet()
    qsos = qso +: qsos
    //    if (qso.qsoKind.isErrant) {
    //      errantQsos = errantQsos :+ qso
    //    }
  }

  def result: QsoResult = {
    QsoResult(
      byBand = bandCountMap.map { case (band, ai) => BandCount(band, ai.get()) }.toSeq,
      byMode = modeCountMap.map { case (mode, ai) => ModeCount(mode, ai.get()) }.toSeq,
      modeBand = modeBands.result().toSeq,
      qsoScore,
      errantQsosBuilder.result(),
      qsos,
      qsoKinds.result
    )
  }
}






