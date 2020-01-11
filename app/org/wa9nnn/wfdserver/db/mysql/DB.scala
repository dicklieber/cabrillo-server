
package org.wa9nnn.wfdserver.db.mysql

import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import org.wa9nnn.cabrillo.requirements.Frequencies
import org.wa9nnn.wfdserver.db.mysql.Tables._
import org.wa9nnn.wfdserver.db.{DBService, mysql}
import org.wa9nnn.wfdserver.htmlTable.{Header, Row, Table}
import org.wa9nnn.wfdserver.model.{Categories, LogInstance, Qso, StationLog}
import org.wa9nnn.wfdserver.{CallSignId, model}
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

    val callSign = logInstance.stationLog.callSign

    val query = for {
      maybeHighestVersion <- Entries.filter(_.callsign === callSign).map(_.logVersion).max.result
      entryId <- Entries returning Entries.map(_.id) += adapter.entryRow(maybeHighestVersion)
      _ <- Contacts ++= adapter.contactsRows(entryId)
      _ <- Soapboxes ++= adapter.soapboxes(entryId)
    } yield {
      println(maybeHighestVersion)
      entryId
    }
    Await.ready(db.run(query), 10 seconds)
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
   * ignores case
   *
   * @param partialCallSign
   * @return
   */
  override def search(partialCallSign: String): Future[Seq[CallSignId]] = {
    throw new NotImplementedError() //todo
  }

  override def stats: Future[Table] = {
    val rows: Future[Seq[Row]] = statsGenerator()
    rows.map(
      Table(Header("Statistics", "Item", "Value"), _).withCssClass("resultTable")
    )
  }

  private implicit def stringToOptionString(s: String): Option[String] = {
    if (s.isEmpty) {
      None
    } else {
      Option(s)
    }
  }

  private implicit def optionStringToString(s: Option[String]): String = {
    s.getOrElse("")
  }
  private implicit def boolTo(s: Option[String]): String = {
    s.getOrElse("")
  }

  override def logInstance(sentryId: String): Future[Option[LogInstance]] = {
    val entryId: Int = sentryId.toInt
    db.run(for {
      entriesRow <- Entries.filter(_.id === entryId).result
      soapboxes <- Soapboxes.filter(_.entryId === entryId).result
      contacts <- Contacts.filter(_.entryId === entryId).result
    } yield {

      entriesRow.headOption.map { er =>
        val callSign = er.callsign
        val stationLog = StationLog(
          callSign = callSign,
          club = er.club,
          createdBy = er.createdBy,
          location = er.location,
          arrlSection = er.arrlSection,
          category = er.category,
          certificate = if(er.certificate) Option("YES") else None,
          address = List(er.address), //todo probably have only one
          city = er.city,
          stateProvince = er.stateProvince,
          postalCode = er.postalcode,
          country = er.country,
          categories = Categories(
            operator = er.operators.toString,
            station = Option(Station(er.stationId)),
            transmitter = Option(Transmitter(er.transmitterId)),
            power = Power(er.powerId),
            assisted = org.wa9nnn.wfdserver.db.mysql.Assisted(er.assistedId),
            overlay = Overlay(er.overlayId),
            time = mysql.Time(er.timeId, ""),
            band = Band(er.bandId, ""),
            mode = Mode(er.modeId, "")
          ),
          soapBoxes = soapboxes.map(_.soapbox).toList,
          email = er.email,
          gridLocator = er.gridLocator,
          name = er.name,
          claimedScore = Option(er.claimedScore)
        )
        val sent = model.Exchange(
          callSign = stationLog.callSign.get,
          category = stationLog.category,
          section = stationLog.arrlSection.get
        )
        val qsos = contacts.map { cr: _root_.org.wa9nnn.wfdserver.db.mysql.Tables.ContactsRow =>
          Qso(
            b = Frequencies.check(cr.freq),
            m = Mode(cr.qsoMode),
            ts = {
              org.wa9nnn.wfdserver.util.TimeConverters.sqlToInstant(cr.contactDate, cr.contactTime)
            },
            s = sent,
            r = model.Exchange(
              callSign = cr.callsign,
              category = cr.category,
              section = cr.sect
            )
          )
        }

        LogInstance(_id = sentryId,
          logVersion = er.logVersion,
          qsoCount = qsos.length,
          stationLog = stationLog,
          qsos = qsos
        )
      }
    }
    )
  }
}





