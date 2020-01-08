
package org.wa9nnn.wfdserver.db

import be.objectify.deadbolt.scala.AuthenticatedRequest
import com.google.inject.Injector
import com.typesafe.config.Config
import javax.inject.{Inject, Singleton}
import org.wa9nnn.cabrillo.model.CabrilloData
import org.wa9nnn.wfdserver.CallSignId
import org.wa9nnn.wfdserver.db.mongodb.{DB => MongoDB}
import org.wa9nnn.wfdserver.db.mysql.{DB => SlickDB}
import org.wa9nnn.wfdserver.htmlTable.Table
import org.wa9nnn.wfdserver.model.LogInstance
import org.wa9nnn.wfdserver.util.JsonLogging
import play.api.mvc.{AnyContent, Request, Session}

import scala.concurrent.Future

/**
 * Converts a [[CabrilloData]] to a [[LogInstance]] and invokes all the enabled datebases.
 * @param config all configuration.
 * @param injector guice
 */
@Singleton
class DBRouter @Inject()(config: Config, injector: Injector) extends JsonLogging {
  private val dbs: Map[String, DBService] = {
    val builder = Map.newBuilder[String, DBService]
    builder += "MongoDB" -> new MongoDB(config.getConfig("mongodb"))
    try {
      val slickConfig = config.getConfig("slick.dbs.default")
      if (slickConfig.getBoolean("enable")) {
        val db = injector.getInstance(classOf[SlickDB])
        builder += "MySQL" -> db
      }
    } catch {
      case e: Exception =>
        logJson("slick db setup").error(e)
    }

    builder.result()
  }
  if (dbs.isEmpty) {
    logger.error("No db configured, shutting down")
    System.exit(1)
  }

  def ingest(cabrilloData: CabrilloData): DbIngestResult = {
    val logInstance: LogInstance =  LogInstanceAdapter(cabrilloData) //todo before Mongo-specific stuff

    dbs.values.map(_.ingest(logInstance)).head // use Key from last one, mongodb)0
  }

  private def db(name: Option[String]): DBService = {
    name.map(dbs.getOrElse(_, dbs.values.head)).getOrElse(dbs.values.head)
  }

  def callSignIds(database: Option[String]): Future[Seq[CallSignId]] = {
    db(database).callSignIds
  }

  def entry(entryId: String, database: Option[String]): Future[Option[EntryViewData]] = {
    db(database).entry(entryId)
  }

  def stats(database: Option[String]): Future[Table] = {
    db(database).stats
  }

  def dbNames: Seq[String] = dbs.keys.toSeq.sorted
}

object DBRouter {
  val dbSessionKey = "db"

  def dbFromSession(implicit request: Request[AnyContent]): Option[String] = {
    request.session.get(dbSessionKey)
  }

  def dbToSession(dbName: String)(implicit request: AuthenticatedRequest[AnyContent]): Session = {
    request.session + (dbSessionKey -> dbName)
  }
}

trait DBService {
  /**
   *
   * @param logInstance in coming.
   * @return LogInstance with logVersion.
   */
  def ingest(logInstance: LogInstance): LogInstance

  def callSignIds: Future[Seq[CallSignId]]

  def entry(entryId: String): Future[Option[EntryViewData]]

  /**
   * Actual state vari depending on the database used.
   * @return
   */
  def stats: Future[Table]
}

trait DbIngestResult {
  def id: String
}
