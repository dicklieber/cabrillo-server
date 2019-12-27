
package org.wa9nnn.wfdserver.db.mysql

import java.time.LocalDateTime

import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import org.wa9nnn.cabrillo.model.CabrilloData
import org.wa9nnn.wfdserver.CallSignId
import org.wa9nnn.wfdserver.db.mysql.Tables._
import org.wa9nnn.wfdserver.db.{DBService, EntryViewData}
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

  def ingest(cabrilloData: CabrilloData): String = {
    val adapter = CabrilloDataAdapter(cabrilloData)

    val callsign = adapter.callsign

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
    Await.result[Int](eventualInt, 10 seconds).toString
  }

  def entries: Future[Seq[EntriesRow]] = {
    db.run(Entries.result)
    //    Await.result[Seq[EntriesRow]](resultingUsers, 10 seconds)
  }

  def callSignIds: Future[Seq[CallSignId]] = {
    //todo this is crude, gets all entire EntriedRows, but I killed too many hour trying to get Slick to return just the columns we need.
    entries.map { s =>
      (for {
        er <- s
        if er.callsign.isDefined
      } yield {
        CallSignId(er.callsign.get, er.logVersion.getOrElse(0), er.id)
      }).sortBy(r => (r.callsign, r.logVersion))
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
      val soaps: Seq[String] = soapboxes.iterator.flatMap(_.soapbox).toSeq
      //      EntryTables(entriesRow.head, soaps, contacts)
      entriesRow.headOption.map { er: _root_.org.wa9nnn.wfdserver.db.mysql.Tables.EntriesRow =>
        val contactRows: Seq[Row] = contacts.map((c: _root_.org.wa9nnn.wfdserver.db.mysql.Tables.ContactsRow) => {
          val exchanges = c.exch.split(org.wa9nnn.wfdserver.db.mysql.CabrilloDataAdapter.exchSeperator)
          val instant = LocalDateTime.of(c.contactDate.toLocalDate, c.contactTime.toLocalTime)
          Row(
            //"Freq", "Mode", "Sent", "Received")
            c.freq,
            Mode(c.qsoMode),
            Cell(exchanges(0)).withCssClass("exchange"),
            Cell(exchanges(1)).withCssClass("exchange"),
            instant
          )
        }
        )
        EntryViewData(EntriesRowSource(er, soaps), contactRows, er.callsign.get, er.club)
      }
    }
    )
  }

  override def stats: Future[Table]

  = {
    statsGenerator().map { rows: Seq[Row] =>
      val table: Table = Table(Header("Statistics", "Item", "Value"), rows: _*).withCssClass("resultTable")

      val withLocations = table.copy(rows = table.rows :++ statsGenerator.aggregates())
      withLocations
    }

  }

}





