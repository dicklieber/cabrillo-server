
package org.wa9nnn.cabrilloserver.db.mysql

import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import org.wa9nnn.cabrillo.model.CabrilloData
import org.wa9nnn.cabrilloserver.db.mysql
import org.wa9nnn.cabrillo.model.CabrilloTypes.Tag
import slick.dbio.Effect
import slick.jdbc.MySQLProfile.api._
import slick.sql.FixedSqlAction

import scala.concurrent.Future

@Singleton
class MySqlIngester @Inject() extends LazyLogging {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global //todo probablhy want one specifically for database

  val db = Database.forConfig(path = "wfdmysql")

  implicit def opt(tag: Tag)(implicit cabrilloData: CabrilloData): Option[String] = {
      // get body of 1st instance of the [[Tag]] or None if tag not present
    cabrilloData.apply(tag).headOption.map(_.body)
  }

  def apply(implicit cabrilloData: CabrilloData): Future[Int] = {

    val row: mysql.Tables.EntriesRow = Tables.EntriesRow(
      id = 0, // will come from DB
      logVersion = opt("START-OF-LOG").map(_.toDouble.toInt),
      callsign = opt("CALLSIGN"),
      contest = opt("CONTEST"),
      assisted = None,
      bandId = Some(0),
      modeId = Some(0),
      operators = Some(2),
      operatorTypeId = None,
      powerId = Some(1),
      stationId = None,
      timeId = None
    )
    val value = Tables.Entries returning Tables.Entries.map(_.id) += row
//    val value: FixedSqlAction[Int, NoStream, Effect.Write] = Tables.Entries += row
//    val statement: String = Tables.Entries.insertStatement
    val eventualInt: Future[Int] = db.run(value)
    //    eventualInt.onComplete { f =>
    //      println(f)
    //
    //    }


  eventualInt
  }
}
