
package org.wa9nnn.wfdserver.db.mongodb

import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.bson.types.ObjectId
import org.mongodb.scala._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Sorts._
import org.wa9nnn.wfdserver.CallSignId
import org.wa9nnn.wfdserver.auth.WfdSubject
import org.wa9nnn.wfdserver.db._
import org.wa9nnn.wfdserver.db.mongodb.Helpers._
import org.wa9nnn.wfdserver.htmlTable.Table
import org.wa9nnn.wfdserver.model.WfdTypes.CallSign
import org.wa9nnn.wfdserver.model._

import scala.concurrent.Future
import scala.language.postfixOps

/**
 *
 * @param connectUri see https://docs.mongodb.com/manual/reference/connection-string/
 */
class DB(connectUri: String, dbName: String = "wfd-test") extends DBService {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  private val customCodecs: CodecRegistry = fromProviders(
    // These tell the Scala MongoDB driver which case classes we will be using.
    // This lets Mongo automatically map between scala case classes and mongos BSON document objects.
    classOf[LogInstance],
    classOf[Categories],
    classOf[CallCatSect],
    classOf[StationLog],
    classOf[Exchange],
    classOf[Qso],
    classOf[Agg],
  )
  private val codecRegistry = fromRegistries(customCodecs,
    DEFAULT_CODEC_REGISTRY)

  private val mongoClient: MongoClient = MongoClient(connectUri)
  val database: MongoDatabase = mongoClient.getDatabase(dbName).withCodecRegistry(codecRegistry).withWriteConcern(WriteConcern.MAJORITY)
  private val logCollection: MongoCollection[LogInstance] = database.getCollection("logs")
  private val previousCollection: MongoCollection[LogInstance] = database.getCollection("replaced")
  private val logDocumentCollection = database.getCollection("logs")

  logCollection.createIndex(ascending("stationLog.callSign")).results()

  val statsGenerator: StatsGenerator = new StatsGenerator(database)

  /**
   *
   * @param logInstance in coming.
   * @return database key for this data. This is always a string. For Mongo it's he hex version of the [[ObjectId]]
   */
  override def ingest(logInstance: LogInstance): LogInstance = {

    val callSign = logInstance.stationLog.callSign

    val maybePrevious: Option[LogInstance] = logCollection.find(equal("stationLog.callSign", callSign))
      .results()
      .headOption
    val logVersion = maybePrevious.map { previous =>
      // copy to previousCollection
      previousCollection.insertOne(previous).results()
      // delete from main
      logCollection.deleteOne(equal("_id", previous._id)).results()
      previous.logVersion + 1
    }.getOrElse(1)

    val logInstanceWithLogVersion = {
      if (logInstance.logVersion != logVersion) {
        logInstance.copy(logVersion = logVersion)
      } else {
        logInstance
      }
    }
    logCollection.insertOne(logInstanceWithLogVersion).results()
    logInstance
  }

  override def callSignIds()(implicit subject:WfdSubject): Future[Seq[CallSignId]] = {
    logDocumentCollection.find()
      .projection(include("stationLog.callCatSect.callSign", "stationLog.logVersion", "_id"))
      .sort(ascending("stationLog.callCatSect.callSign"))
      .toFuture()
      .map { s => s.map(CallSignId(_)) }
  }

  override def logInstance(entryId: String)(implicit subject:WfdSubject): Future[Option[LogInstance]] = {
    logCollection.find(equal("_id", entryId)).first.toFutureOption()
  }
  override def getLatest(callSign: CallSign)(implicit subject: WfdSubject): Future[Option[LogInstance]] = {
    logCollection.find(equal("stationLog.callSign", callSign)).first().toFutureOption()
  }

  override def stats()(implicit subject:WfdSubject): Future[Table] = {
    Future(
      statsGenerator()
    )
  }

  /**
   * ignores case
   *
   * @param partialCallSign to search for.
   * @return matching.
   */
  override def search(partialCallSign: String)(implicit subject:WfdSubject): Future[Seq[CallSignId]] = {
    val ucCallSign = partialCallSign.toUpperCase()
    logDocumentCollection.find(regex("stationLog.callCatSect.callSign", s"""$ucCallSign"""))
      .projection(include("stationLog.callCatSect.callSign", "stationLog.logVersion", "_id"))
      .sort(ascending("stationLog.callCatSect.callSign"))
      .toFuture()
      .map { s =>
        s.map(CallSignId(_))
      }

  }

  override def recent()(implicit subject:WfdSubject): Future[Seq[CallSignId]] = {
    logDocumentCollection.find()
      .projection(include("stationLog.callSign", "stationLog.logVersion", "_id"))
      .sort(descending("stationLog.ingested"))
      .limit(recentLimit)
      .toFuture()
      .map { s =>
        s.map(CallSignId(_))
      }

  }

}
