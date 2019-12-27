
package org.wa9nnn.wfdserver.db.mysql

import java.util.concurrent.atomic.AtomicInteger

import javax.inject.{Inject, Singleton}
import org.wa9nnn.wfdserver.db.mysql.Tables._
import org.wa9nnn.wfdserver.htmlTable.{Cell, Row}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import scala.collection.concurrent.TrieMap
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

@Singleton
class StatsGenerator @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global //todo probably want one specifically for database

  def apply(): Future[Seq[Row]] = {

    Future.sequence(Seq(
      db.run(Entries
        .length
        .result.map(c => Row("Total Submissions", c))),
      db.run(Entries.map(f => f.callsign)
        .distinct
        .length
        .result.map(c => Row("Callsigns", c))),
      db.run(Contacts.length.result.map(c => Row("Total Contacts", c))),

    ))
  }

  def aggregates(): Seq[Row] = {
    val aggregator = new Aggregator()
    val c: Seq[EntriesRow] = Await.result(db.run(Entries.result), 5 seconds)
    c.groupBy(_.callsign)
      .foreach { case (_, entries) =>
        aggregator(entries.maxBy(_.logVersion))
      }
    aggregator.result
  }
}

class Aggregator {
  var entryCount = 0
  val locationCounts = new MappedCounter

  def apply(entriesRow: EntriesRow): Unit = {
    entryCount += 1
    entriesRow.location.foreach { e => locationCounts.apply(e) }
  }

  def result: Seq[Row] = {
    Row(Seq(Cell("Locations").withColSpan(2).withCssClass("sectionHeader"))) +: locationCounts.results
  }
}

class MappedCounter {
  private val map = new TrieMap[String, AtomicInteger]

  def apply(name: String): Unit = {
    map.getOrElseUpdate(name, new AtomicInteger()).getAndIncrement()
  }

  def results: Seq[Row] = {
    map
      .iterator
      .toSeq
      .sortBy(_._2.get())
      .reverse
      .map { case (name, count) =>
        Row(name, count)
      }
  }
}