
package org.wa9nnn.wfdserver.db.mongodb

import org.mongodb.scala.MongoCollection
import org.wa9nnn.wfdserver.htmlTable.{Row, RowsSource}
import org.mongodb.scala._
import org.mongodb.scala.model.Aggregates._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Sorts._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.model._
import org.mongodb.scala.model.Accumulators._
import org.wa9nnn.wfdserver.db.mongodb.Helpers._

import scala.concurrent.Future
import scala.language.postfixOps

//b.logs.aggregate({$group: {_id: "$stationLog.category", total: { $sum: 1 }}})
//{ "_id" : "1O", "total" : 1 }
//{ "_id" : "6O", "total" : 1 }
//> db.logs.aggregate({$group: {_id: "$stationLog.location", total: { $sum: 1 }}})
//{ "_id" : "AL", "total" : 1 }
//{ "_id" : "IL", "total" : 1 }


class StatsGenerator(database: MongoDatabase) {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global //todo probably want one specifically for database

  val aggCollection: MongoCollection[Agg] = database.getCollection[Agg]("logs")

  def apply(): Seq[Row] = {

    def aggragate(field: String): Row = {
      val f = "$" + field
      val r: Agg = aggCollection.aggregate(Seq(group(f, sum("count", 1)))).headResult()
      r.toRow
    }

    Seq(
      Row("Logs", aggCollection.countDocuments().headResult()),
      //todo qsos
      aggragate("stationLog.category"),
      aggragate("stationLog.location"),
    )
  }
}

case class Agg(_id: String, count: Int) {
  def toRow: Row = {
    Row(_id, count)
  }
}



