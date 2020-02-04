
package org.wa9nnn.wfdserver.db

import com.google.inject.Injector
import com.typesafe.config.Config
import javax.inject.{Inject, Singleton}
import org.wa9nnn.wfdserver.CallSignId
import org.wa9nnn.wfdserver.auth.WfdSubject
import org.wa9nnn.wfdserver.db.mongodb.{DB => MongoDB}
import org.wa9nnn.wfdserver.db.mysql.{DB => SlickDB}
import org.wa9nnn.wfdserver.htmlTable.Table
import org.wa9nnn.wfdserver.model.LogInstance
import org.wa9nnn.wfdserver.model.WfdTypes.CallSign
import org.wa9nnn.wfdserver.scoring.ScoreRecord
import org.wa9nnn.wfdserver.util.JsonLogging

import scala.concurrent.Future

/**
 * Invokes all the enabled databases.
 *
 * @param config   all configuration.
 * @param injector guice
 */
@Singleton
class DBRouter @Inject()(config: Config, injector: Injector) extends JsonLogging  with DBService {

  private val dbs: Map[String, DBService] = {
    val builder = Map.newBuilder[String, DBService]
    if (config.getBoolean("mongodb.enable")) {
      val uri = config.getString("mongodb.mongoURI")
      builder += "MongoDB" -> new MongoDB(uri, "wfd")
    }
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


  val dbNames: Seq[String] = dbs.keys.toSeq.sorted


  def ingest(logInstance: LogInstance): LogInstance = {
    dbs.values.map(_.ingest(logInstance)).head // use Key from last one, mongodb)0
  }

  private def db()(implicit subject: WfdSubject): DBService = {

    try {
      dbs.getOrElse(subject.dbName, throw new IllegalArgumentException(s"Unknown dbName: ${subject.dbName}"))
    } catch {
      case e: Exception =>
        dbs(dbNames.head)
    }
  }

  def callSignIds()(implicit subject: WfdSubject): Future[Seq[CallSignId]] = {
    db().callSignIds
  }

  def recent()(implicit subject: WfdSubject): Future[Seq[CallSignId]] = {
    db().recent
  }

  def search(partialCallsign: String)(implicit subject: WfdSubject): Future[Seq[CallSignId]] = {
    db.search(partialCallsign)
  }

  def logInstance(entryId: String)(implicit subject: WfdSubject): Future[Option[LogInstance]] = {
    db.logInstance(entryId)
  }

  def stats()(implicit subject: WfdSubject): Future[Table] = {
    db.stats
  }

  override def getLatest(callSign: CallSign)(implicit subject: WfdSubject): Future[Option[LogInstance]] = {
    db.getLatest(callSign)
  }

  override def stationCount()(implicit subject: WfdSubject): Int ={
    db.stationCount
  }

  override def dropScoringDb()(implicit subject: WfdSubject): Unit = db.dropScoringDb()

  override def putScore(scoreRecord: ScoreRecord)(implicit subject: WfdSubject): Unit = db.putScore(scoreRecord)

  override def putScores(ranked: Seq[ScoreRecord])(implicit subject: WfdSubject): Unit = db.putScores(ranked)

  override def getScores(scoreFilter: ScoreFilter)(implicit subject: WfdSubject):Future[Seq[ScoreRecord]] = db.getScores(scoreFilter)

}




