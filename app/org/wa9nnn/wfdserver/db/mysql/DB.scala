
package org.wa9nnn.wfdserver.db.mysql

import java.time.LocalDateTime

import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import org.wa9nnn.wfdserver.CallSignId
import org.wa9nnn.wfdserver.db.mysql.Tables._
import org.wa9nnn.wfdserver.db.{DBService, DbIngestResult, EntryViewData, LogInstance, mysql}
import org.wa9nnn.wfdserver.htmlTable.{Cell, Header, Row, Table}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

//@Singleton
class DB @Inject()(@Inject() protected val dbConfigProvider: DatabaseConfigProvider, statsGenerator: StatsGenerator)
  extends LazyLogging
    with HasDatabaseConfigProvider[JdbcProfile]
    with DBService {


  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global //todo probably want one specifically for database

  def ingest(logInstance: LogInstance): LogInstance = {
    val adapter = MySQLDataAdapter(logInstance)

    val callsign = logInstance.stationLog.callSign

    val query = for {
      maybeHighestVersion <- Entries.filter(_.callsign === callsign).map(_.logVersion).max.result
      entryId <- Entries returning Entries.map(_.id) += adapter.entryRow(maybeHighestVersion)
      _ <- Contacts ++= adapter.contactsRows(entryId)
      _ <- Soapboxes ++= adapter.soapboxes(entryId)
    } yield {
      println(maybeHighestVersion)
      entryId
    }
    val eventualInt = db.run(query)
    MySqlIngestResult(Await.result[Int](eventualInt, 10 seconds))
    logInstance //todo handle logVersion
  }

  def entries: Future[Seq[EntriesRow]] = {
    db.run(Entries.result)
    //    Await.result[Seq[EntriesRow]](resultingUsers, 10 seconds)
  }

  def callSignIds: Future[Seq[CallSignId]] = {
    //todo this is crude, gets all entire EntriedRows, but I killed too many hour trying to get Slick to return just the columns we need.
    entries.map { s: Seq[mysql.Tables.EntriesRow] =>
      s.map { er => CallSignId(er.callsign, er.logVersion, er.id) }
        .sorted
    }
  }

  /**
   * Fetches one entry along with the contacts and soapboxes
   */
  def entry(sEntryId: String): Future[Option[EntryViewData]] = {
    val entryId: Int = sEntryId.toInt
    db.run(for {
      entriesRow <- Entries.filter(_.id === entryId).result
      soapboxes <- Soapboxes.filter(_.entryId === entryId).result
      contacts <- Contacts.filter(_.entryId === entryId).result
    } yield {
      val soaps: Seq[String] = soapboxes.map(_.soapbox)
      //      EntryTables(entriesRow.head, soaps, contacts)
      entriesRow.headOption.map { er: _root_.org.wa9nnn.wfdserver.db.mysql.Tables.EntriesRow =>
        val contactRows: Seq[Row] = contacts.map((c: _root_.org.wa9nnn.wfdserver.db.mysql.Tables.ContactsRow) => {
          //          val exchanges: Array[String] = c.exch.split(org.wa9nnn.wfdserver.db.mysql.MySQLDataAdapter.exchSeperator)
          val instant = LocalDateTime.of(c.contactDate.toLocalDate, c.contactTime.toLocalTime)
          Row(
            //"Freq", "Mode", "Sent", "Received")
            c.freq,
            Mode(c.qsoMode),
            Cell("todo").withCssClass("exchange"),
            Cell(c.exch).withCssClass("exchange"),
            instant
          )
        }
        )
        EntryViewData(EntriesRowSource(er, soaps), contactRows, er.callsign, Some(er.club))
      }
    }
    )
  }

  override def stats: Future[Table] = {
    val rows: Future[Seq[Row]] = statsGenerator()
    rows.map(
      Table(Header("Statistics", "Item", "Value"), _: _*).withCssClass("resultTable")
    )
  }


}

case class MySqlIngestResult(intId: Int) extends DbIngestResult {
  override def id: String = intId.toString
}



