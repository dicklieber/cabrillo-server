
package org.wa9nnn.wfdserver.scoring

import com.typesafe.config.Config
import javax.inject.Inject
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase, Observer, WriteConcern}
import org.wa9nnn.wfdserver.Timer
import org.wa9nnn.wfdserver.db.mongodb.DB.codecRegistry
import org.wa9nnn.wfdserver.model._
import org.wa9nnn.wfdserver.util.JsonLogging

import scala.collection.concurrent.TrieMap
import scala.concurrent.duration._
import scala.concurrent.{Await, Promise}
import scala.language.postfixOps
import scala.util.Try

/**
 * This is how we find a matching [[Qso]]
 *
 */
class ReceivedQsoCache @Inject()(config: Config) extends JsonLogging {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  private val mongoConfig = config.getConfig("mongodb")
  private val connectUri: String = mongoConfig.getString("mongoURI")
  private val dbName: String = mongoConfig.getString("database")

  private val map = new TrieMap[QsoKey, Qso] // receivedQsoKey to Qso

  var stationCount = 0
  def mapSize: Int = map.size

  private val timer: Timer = Timer()
  private val promise: Promise[String] = Promise()
  private val future = promise.future
  private val mongoClient: MongoClient = MongoClient(connectUri)
  private val database: MongoDatabase = mongoClient.getDatabase(dbName).withCodecRegistry(codecRegistry).withWriteConcern(WriteConcern.MAJORITY)
  private val collection: MongoCollection[LogInstance] = database.getCollection("logs")

  collection.find().subscribe(new Observer[LogInstance] {

    override def onNext(result: LogInstance): Unit = {
      stationCount = stationCount + 1
      result.qsos.foreach { qso =>
        map.put(qso.receivedKey, qso)
      }
    }

    override def onError(e: Throwable): Unit =
      logger.error(s"Reading LogInstance from DB", e)

    override def onComplete(): Unit = {
      promise.complete(Try("Done"))
    }
  })
  logger.debug(s"Waiting for Qsos to load")
  Await.ready(future, 1 hour)
  logger.info(f"Finished Loaded $stationCount stations with ${map.size} qsos in $timer")

  def apply(key: QsoKey): Option[Qso] = {
    map.remove(key)
  }
}
