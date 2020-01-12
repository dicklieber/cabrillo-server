
package org.wa9nnn.wfdserver.db.mongodb

import org.mongodb.scala.{MongoCollection, _}
import org.mongodb.scala.model.Accumulators._
import org.mongodb.scala.model.Aggregates._
import org.wa9nnn.wfdserver.db.mongodb.Helpers._
import org.wa9nnn.wfdserver.htmlTable.{Cell, Header, Row, Table, TableInACell}

import scala.language.postfixOps

//db.logs.aggregate({$group: {_id: "$stationLog.category", total: { $sum: 1 }}})
//{ "_id" : "1O", "total" : 1 }
//{ "_id" : "6O", "total" : 1 }
// db.logs.aggregate({$group: {_id: "$stationLog.location", total: { $sum: 1 }}})
//{ "_id" : "AL", "total" : 1 }
//{ "_id" : "IL", "total" : 1 }


class StatsGenerator(database: MongoDatabase) {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global //todo probably want one specifically for database

  val aggCollection: MongoCollection[Agg] = database.getCollection[Agg]("logs")

  private def aggragate(field: String): Seq[Cell] = {
    val f: String = "$" + field
    val r: Seq[Agg] = aggCollection.aggregate(Seq(group(f, sum("count", 1)))).results()

    def byKind(a1: Agg, a2: Agg) = {
      a1._id > a2._id
    }

    def byCount(a1: Agg, a2: Agg) = {
      a1.count > a2.count
    }

    def aTable(byVal: Boolean) = {
      val rows = if (byVal)
        r.sortWith((s1, s2) => byKind(s1, s2)).reverse
      else
        r.sortWith((s1, s2) => byCount(s1, s2)).reverse

      val shortened = field.reverse.takeWhile(_ != '.').reverse
      TableInACell(Table(Header(shortened, "Value", "Count"), rows.map(_.toRow)).withCssClass("resultTable")
      )
    }

    Seq(aTable(true), aTable(false))
  }

  def apply(): Table = {
    val miscTable: Cell = TableInACell(Table(Header("Misc", "Value", "Count"), Seq(
      Row("Logs", aggCollection.countDocuments().headResult())
      ,
      aggCollection.aggregate(Seq(group("Qsos", sum("count", "$qsoCount")))).headResult().toRow
    )
    )
      .withCssClass("resultTable")
    )

    //    val value = aggCollection.aggregate(Seq(group("Qsos", sum("count", "$qsoCount")))).headResult()


    Table(Seq.empty, Seq(Row(
      miscTable +: aggragate("stationLog.category") :++ aggragate("stationLog.arrlSection")
    ))).withCssClass("resultTable")
  }
}

case class Agg(_id: String, count: Int) {
  def toRow: Row = {
    Row(_id, count)
  }
}



