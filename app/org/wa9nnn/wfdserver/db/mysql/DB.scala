
package org.wa9nnn.wfdserver.db.mysql

import javax.inject._
import nl.grons.metrics4.scala.{DefaultInstrumented, Timer}
import org.wa9nnn.cabrillo.requirements.Frequencies
import org.wa9nnn.wfdserver.auth.WfdSubject
import org.wa9nnn.wfdserver.db.mysql.Tables._
import org.wa9nnn.wfdserver.db.{DBService, mysql}
import org.wa9nnn.wfdserver.htmlTable.{Header, Row, Table}
import org.wa9nnn.wfdserver.model.WfdTypes.CallSign
import org.wa9nnn.wfdserver.model._
import org.wa9nnn.wfdserver.util.JsonLogging
import org.wa9nnn.wfdserver.{CallSignId, model}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

//noinspection SpellCheckingInspection
class DB @Inject()(@Inject() protected val dbConfigProvider: DatabaseConfigProvider, statsGenerator: StatsGenerator)
  extends JsonLogging
    with HasDatabaseConfigProvider[JdbcProfile]
    with DBService
    with DefaultInstrumented {

  val timer: Timer = metrics.timer("DB-MySQl")

  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global //todo probably want one specifically for database

  private def deleteExisting(callSign: CallSign) = {
    val f = for {
      entryId <- db.run(Entries.filter(_.callsign === callSign).result.head.map(_.id))
      _ <- db.run(Contacts.filter(_.entryId === entryId).delete)
      _ <- db.run(Soapboxes.filter(_.entryId === entryId).delete)
      _ <- db.run(Entries.filter(_.id === entryId).delete)
    } yield {

    }
    Await.ready(f, 5 seconds)
  }

  def ingest(logInstance: LogInstance): LogInstance = {
    timer.time {
      val adapter = MySQLDataAdapter(logInstance)

      val callSign = logInstance.stationLog.callSign

      deleteExisting(callSign)

      val query = for {
        entryId <- Entries returning Entries.map(_.id) += adapter.entryRow()
        _ <- Contacts ++= adapter.contactsRows(entryId)
        _ <- Soapboxes ++= adapter.soapboxes(entryId)
      } yield {
        entryId
      }
      Await.ready(db.run(query), 3 minutes)
      logInstance
    }
  }

  def callSignIds()(implicit subject: WfdSubject): Future[Seq[CallSignId]] = {
    db.run(
      sql"""SELECT callsign, id
           FROM WFD.entries
           ORDER BY callsign""".as[(String, Int)])
      .map { rs =>
        rs.map { case (callSign, id) =>
          CallSignId(callSign, id)
        }
      }
  }

  /**
   * ignores case
   *
   * @param partialCallSign to search for.
   * @return results of search.
   */
  override def search(partialCallSign: String)(implicit subject: WfdSubject): Future[Seq[CallSignId]] = {
    db.run(
      sql"""SELECT callsign, id
           FROM WFD.entries
           WHERE callsign REGEXP $partialCallSign
           ORDER BY callsign""".as[(String, Int)])
      .map { rs =>
        rs.map { case (callSign, id) =>
          CallSignId(callSign, id)
        }
      }
  }

  override def recent()(implicit subject: WfdSubject): Future[Seq[CallSignId]] = {
    db.run(
      sql"""SELECT callsign, id
           FROM WFD.entries
           ORDER BY id DESC
           LIMIT $recentLimit""".as[(String, Int)])
      .map { rs =>
        rs.map { case (callSign, id) =>
          CallSignId(callSign, id)
        }
      }

  }

  override def stats()(implicit subject: WfdSubject): Future[Table] = {
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

  override def logInstance(sentryId: String)(implicit subject: WfdSubject): Future[Option[LogInstance]] = {
    val entryId: Int = sentryId.toInt
    db.run(for {
      entriesRow <- Entries.filter(_.id === entryId).result
      soapboxes <- Soapboxes.filter(_.entryId === entryId).result
      contacts <- Contacts.filter(_.entryId === entryId).result
    } yield {

      entriesRow.headOption.map { er =>
        val callSign = er.callsign
        val stationLog = StationLog(
          callCatSect = CallCatSect(callSign, er.category, er.arrlSection),
          club = er.club,
          createdBy = er.createdBy,
          location = er.location,
          certificate = if (er.certificate) Option("YES") else None,
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
          logVersion = er.logVersion.toString,
          claimedScore = Option(er.claimedScore)
        )
        val sent = model.Exchange(stationLog.callCatSect)
        val qsos: Seq[Qso] = contacts.map { cr: _root_.org.wa9nnn.wfdserver.db.mysql.Tables.ContactsRow =>
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
          qsoCount = qsos.length,
          stationLog = stationLog,
          qsos = qsos
        )
      }
    }
    )
  }

  override def getLatest(callSign: CallSign)(implicit subject: WfdSubject): Future[Option[LogInstance]] = {
    throw new NotImplementedError() //todo
  }
}





