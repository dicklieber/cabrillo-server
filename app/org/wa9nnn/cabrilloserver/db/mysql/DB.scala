
package org.wa9nnn.cabrilloserver.db.mysql

import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import org.wa9nnn.cabrillo.model.CabrilloData
import org.wa9nnn.cabrilloserver.CallSignId
import org.wa9nnn.cabrilloserver.db.mysql.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

@Singleton
class DB @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends LazyLogging with HasDatabaseConfigProvider[JdbcProfile] {

  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global //todo probably want one specifically for database

  def apply(cabrilloData: CabrilloData): Int = {
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
    Await.result[Int](eventualInt, 10 seconds)
  }

  def entries: Seq[EntriesRow] = {
    val resultingUsers: Future[Seq[EntriesRow]] = db.run(Entries.result)
    Await.result[Seq[EntriesRow]](resultingUsers, 10 seconds)
  }

  def callSignIds: Seq[CallSignId] = {
    //todo this is crude, gets all entire EntriedRows, but I killed too many hour trying to get Slick to return just the columns we need.
    (for {
      er <- entries
      if er.callsign.isDefined
    } yield {
      CallSignId(er.callsign.get, er.logVersion.getOrElse(0), er.id)
    }).sortBy(r => (r.callsign, r.logversion))
  }

  /**
   * Fetches one entry along with the contacts and soapboxes
   */
  def entry(entryId:Int):Future[Option[Entry]] = {
    db.run(for {
      entriesRow <- Entries.filter(_.id === entryId).result
      soapboxes <- Soapboxes.filter(_.entryId === entryId).result
      contacts <- Contacts.filter(_.entryId === entryId).result
    } yield {
      entriesRow.headOption.map(er =>
        Entry(er, soapboxes.map(_.soapbox.get), contacts)
      )
    })
  }
}





