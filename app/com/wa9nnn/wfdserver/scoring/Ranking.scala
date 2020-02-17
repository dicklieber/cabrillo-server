
package com.wa9nnn.wfdserver.scoring

import scala.collection.concurrent.TrieMap
import scala.collection.mutable

object Ranking {
  def apply(records: Seq[ScoreRecord]): Seq[ScoreRecord] = {
    val overallOrdered = records.sortBy(_.awardedPoints).reverse
    val byCategory = new TrieMap[String, mutable.Builder[ScoreRecord, Seq[ScoreRecord]]]

    overallOrdered.foreach { scoreRecord =>
      val category = scoreRecord.callCatSect.category
      byCategory.getOrElseUpdate(category, Seq.newBuilder[ScoreRecord]) += scoreRecord
    }
    //todo no one could possibly reason this out!!
    val builders = byCategory.map { case (category, builder) => category -> builder.result }
    val catByScoreRecord = builders.map { case (category, scoreRecords) => category -> scoreRecords.zipWithIndex.map { case (scoreRecord, rank) => scoreRecord -> rank }.toMap }


    val withOverAllRank = overallOrdered.zipWithIndex.map { case (scoreRecord, rank) =>
      scoreRecord.copy(
        overallRank = Some(rank + 1),
        categoryRank = Some(catByScoreRecord(scoreRecord.callCatSect.category)(scoreRecord) + 1)
      )
    }

    withOverAllRank
  }
}
