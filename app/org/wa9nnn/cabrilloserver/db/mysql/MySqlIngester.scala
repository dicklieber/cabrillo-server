
package org.wa9nnn.cabrilloserver.db.mysql

import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import org.wa9nnn.cabrillo.model.CabrilloData
import org.wa9nnn.cabrilloserver.db.mysql.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

@Singleton
class MySqlIngester @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends LazyLogging with HasDatabaseConfigProvider[JdbcProfile] {
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
    val entryId = Await.result[Seq[EntriesRow]](resultingUsers, 10 seconds)
    println(entryId)
    entryId
  }

}
