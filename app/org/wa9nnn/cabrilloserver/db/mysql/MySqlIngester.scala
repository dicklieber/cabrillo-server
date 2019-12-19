
package org.wa9nnn.cabrilloserver.db.mysql

import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import org.wa9nnn.cabrillo.model.CabrilloData
import slick.jdbc.MySQLProfile.api._
import org.wa9nnn.cabrilloserver.db.mysql.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.{JdbcBackend, JdbcProfile, MySQLProfile}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.language.postfixOps
@Singleton
class MySqlIngester @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends LazyLogging   with HasDatabaseConfigProvider[JdbcProfile]{
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global //todo probably want one specifically for database


  def apply(cabrilloData: CabrilloData): Int = {
    val adapter = CabrilloDataAdapter(cabrilloData)

    //  val insertQuery =  Entries returning Entries.map(_.id) += adapter.entryRow
    //
    //    insertQuery
    //    for{
    //      eventualId <-Entries returning Entries.map(_.id) += adapter.entryRow
    //      t <- adapter.contactsRows(eventualId)
    //    }yield{
    //t.image
    //    }
    //    db.run(
//    dbConfigProvider.

    val ddd: JdbcBackend#DatabaseDef = db

    val eventualInt: Future[Int] = db.run(Entries returning Entries.map(_.id) += adapter.entryRow)
    //todo should be a way to combine entry,soapbox and contacts without having to wait
    //todo soapbox
    val entryId = Await.result[Int](eventualInt, 10 seconds)
    Await.ready( db.run(Contacts ++= adapter.contactsRows(entryId)), 10 seconds)
    entryId
  }

  def entries:Seq[EntriesRow] = {
    val resultingUsers: Future[Seq[EntriesRow]] = db.run(Entries.result)
    val entryId = Await.result[Seq[EntriesRow]](resultingUsers, 10 seconds)
    println(entryId)
    entryId
  }
}
