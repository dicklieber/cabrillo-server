
package org.wa9nnn.wfdserver.db.mongodb

import com.typesafe.config.Config
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.types.ObjectId
import org.mongodb.scala._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Projections._
import org.wa9nnn.cabrillo.model.{CabrilloData, Exchange}
import org.wa9nnn.cabrillo.parsers.Exchange_WFD
import org.wa9nnn.wfdserver.CallSignId
import org.wa9nnn.wfdserver.db.mongodb.Helpers._
import org.wa9nnn.wfdserver.db.{DBService, EntryViewData}
import org.wa9nnn.wfdserver.htmlTable.{Header, RowsSource, Table}

import scala.concurrent.Future
import scala.language.postfixOps

/**
 *
 * @param config mongodb section of application.conf
 */
class DBReal(config: Config) extends DBService {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  private val customCodecs = fromProviders(
    classOf[LogInstance],
    classOf[Categories],
    classOf[StationLog],
    classOf[QSO],
    classOf[Exchange_WFD],
    classOf[Agg],
  )
  private val codecRegistry = fromRegistries(customCodecs,
    DEFAULT_CODEC_REGISTRY)

  private val mongoClient: MongoClient = MongoClient()
  private val database: MongoDatabase = mongoClient.getDatabase("wfd").withCodecRegistry(codecRegistry).withWriteConcern(WriteConcern.MAJORITY)
  private val logCollection: MongoCollection[LogInstance] = database.getCollection("logs")
  private val previousCollection: MongoCollection[LogInstance] = database.getCollection("replaced")
  private val logDocumentCollection = database.getCollection("logs")

  val statsGenerator: StatsGenerator = new StatsGenerator(database)

  /**
   *
   * @param cabrilloData in coming.
   * @return database key for this data. This is always a string. For Mongo it's he hex version of the [[ObjectId]]
   */
  override def ingest(cabrilloData: CabrilloData): LogInstance = {

    val callSign = cabrilloData("CALLSIGN").head.body

    val maybePrevious: Option[LogInstance] = logCollection.find(equal("stationLog.callSign", callSign))
      .results()
      .headOption
    val logVersion = maybePrevious.map { previous =>
      // copy to previousCollection
      previousCollection.insertOne(previous).results()
      // delete from main
      logCollection.deleteOne(equal("_id", previous._id)).results()
      previous.stationLog.logVersion + 1
    }.getOrElse(1)

    val adapter = new MongoAdapter(cabrilloData, logVersion)

    adapter.logInstance
//    logCollection.insertOne(logInstance).results()
//    adapter.logId.toHexString

  }

  override def callSignIds: Future[Seq[CallSignId]] = {
    logDocumentCollection.find()
      .projection(include("stationLog.callSign", "stationLog.logVersion", "_id"))
      .toFuture()
      .map { s => s.map(CallSignId(_)) }
  }

  override def entry(entryId: String): Future[Option[EntryViewData]] = {
    val id = new ObjectId(entryId)
    logCollection.find(equal("_id", id)).first.toFutureOption().map(_.map { logInstance =>
      val stationLog = logInstance.stationLog
      val rowsSource: RowsSource = stationLog
      EntryViewData(rowsSource,
        logInstance.qsos.map(_.toRow),
        stationLog.callSign,
        stationLog.club)
    })
  }


  override def stats: Future[Table] = {
    val rows = statsGenerator()
    Future(
      Table(Header("Statistics", "Item", "Value"), rows: _*).withCssClass("resultTable")
    )
  }
}
