
package org.wa9nnn.wfdserver.db.mongodb

import com.typesafe.config.Config
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.types.ObjectId
import org.mongodb.scala._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Projections._
import org.wa9nnn.cabrillo.model.CabrilloData
import org.wa9nnn.cabrillo.parsers.Exchange_WFD
import org.wa9nnn.wfdserver.CallSignId
import org.wa9nnn.wfdserver.db.mongodb.Helpers._
import org.wa9nnn.wfdserver.db.{DBService, EntryViewData}
import org.wa9nnn.wfdserver.htmlTable.{RowsSource, Table}

import scala.concurrent.Future
import scala.language.postfixOps

/**
 *
 * @param config mongodb section of application.conf
 */
class DB(config: Config) extends DBService {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  private val customCodecs = fromProviders(
    classOf[Categories],
    classOf[StationLog],
    classOf[QSO],
    classOf[Exchange_WFD],
  )
  private val codecRegistry = fromRegistries(customCodecs,
    //      javaCodecs,
    DEFAULT_CODEC_REGISTRY)

  private val mongoClient: MongoClient = MongoClient()
  private val database: MongoDatabase = mongoClient.getDatabase("wfd-dev").withCodecRegistry(codecRegistry).withWriteConcern(WriteConcern.MAJORITY)
  private val logCollection: MongoCollection[StationLog] = database.getCollection("logs")
  private val logDocumentCollection = database.getCollection("logs")
  private val qsoCollection: MongoCollection[QSO] = database.getCollection("qsos")

  /**
   *
   * @param cabrilloData in coming.
   * @return database key for this data. This is always a string. For MySQL it will
   */
  override def ingest(cabrilloData: CabrilloData): String = {

    val callSign = cabrilloData("CALLSIGN").head.body

    val logVersion = logDocumentCollection.countDocuments(equal("callSign", callSign)).headResult().toInt
    //    val logVersion  = Await.result[Long](value.toFuture(), 5 seconds).toInt


    val adapter = new MongoAdapter(cabrilloData)
    val qsos = adapter.qsos


    val stationLog: StationLog = adapter.stationLog(logVersion)
    logCollection.insertOne(stationLog).results()
    qsoCollection.insertMany(qsos).results()
    stationLog._id.toHexString

  }

  override def callSignIds: Future[Seq[CallSignId]] = {
    logDocumentCollection.find()
      .projection(include("callSign", "logVersion", "_id"))
      .toFuture()
      .map { s => s.map(CallSignId(_)) }
  }

  override def entry(entryId: String): Future[Option[EntryViewData]] = {
    val id = new ObjectId(entryId)

    val future: Future[Seq[StationLog]] = logCollection.find(equal("_id", id)).toFuture()

    future.map(_.headOption
      .map { stationLog: StationLog =>
        val qsos: Seq[QSO] = qsoCollection.find(equal("logId", id)).results()
        val rowsSource:RowsSource = stationLog
        EntryViewData(rowsSource,
          qsos.map(_.toRow),
          stationLog.callSign,
          stationLog.club)
      })
  }

  override def stats: Future[Table] = {
    throw new NotImplementedError() //todo
  }
}
