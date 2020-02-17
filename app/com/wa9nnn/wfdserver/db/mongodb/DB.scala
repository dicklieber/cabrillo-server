
package com.wa9nnn.wfdserver.db.mongodb

import nl.grons.metrics4.scala.{DefaultInstrumented, Timer}
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.bson.types.ObjectId
import org.mongodb.scala._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Sorts._
import com.wa9nnn.wfdserver.CallSignId
import com.wa9nnn.wfdserver.auth.WfdSubject
import com.wa9nnn.wfdserver.db._
import com.wa9nnn.wfdserver.db.mongodb.Helpers._
import com.wa9nnn.wfdserver.htmlTable.Table
import com.wa9nnn.wfdserver.model.WfdTypes.CallSign
import com.wa9nnn.wfdserver.model._
import com.wa9nnn.wfdserver.scoring.{ScoreRecord, _}

import scala.concurrent.Future
import scala.language.postfixOps
import DB._

/**
 *
 * @param connectUri see https://docs.mongodb.com/manual/reference/connection-string/
 */
class DB(connectUri: String = "mongodb://localhost", dbName: String = "wfd-test") extends DBService with DefaultInstrumented {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  val timer: Timer = metrics.timer("DB-MongoDB")


  private val mongoClient: MongoClient = MongoClient(connectUri)
  val database: MongoDatabase = mongoClient.getDatabase(dbName).withCodecRegistry(codecRegistry).withWriteConcern(WriteConcern.MAJORITY)
  private val logCollection: MongoCollection[LogInstance] = database.getCollection("logs")
  private val previousCollection: MongoCollection[LogInstance] = database.getCollection("replaced")
  private val logDocumentCollection = database.getCollection("logs")

  val statsGenerator: StatsGenerator = new StatsGenerator(database)

  /**
   *
   * @param logInstance in coming.
   * @return database key for this data. This is always a string. For Mongo it's he hex version of the [[ObjectId]]
   */
  override def ingest(logInstance: LogInstance): LogInstance = {
    timer.time {
      val callSign = logInstance.stationLog.callSign

      val maybePrevious: Option[LogInstance] = logCollection.find(equal("_id", callSign))
        .results()
        .headOption
      maybePrevious.foreach { previous =>
        // copy to previousCollection, replacing callSign _id with a new ObjecgtId.
        previousCollection.insertOne(previous.copy(_id = new ObjectId().toHexString)).results()
        // delete from main
        logCollection.deleteOne(equal("_id", previous._id)).results()
      }

      logCollection.insertOne(logInstance).results()
      logInstance
    }
  }

  private val callSignIdInclude: Bson = include("stationLog.callCatSect.callSign", "logVersion", "_id")

  override def callSignIds()(implicit subject: WfdSubject): Future[Seq[CallSignId]] = {
    logDocumentCollection.find()
      .projection(callSignIdInclude)
      .sort(ascending("_id"))
      .toFuture()
      .map { s =>
        s.map {
          CallSignId(_)
        }
      }
  }

  override def logInstance(entryId: String)(implicit subject: WfdSubject): Future[Option[LogInstance]] = {
    logCollection.find(equal("_id", entryId)).first.toFutureOption()
  }

  override def getLatest(callSign: CallSign)(implicit subject: WfdSubject): Future[Option[LogInstance]] = {
    logCollection.find(equal("stationLog.callCatSect.callSign", callSign)).first().toFutureOption()
  }

  override def stats()(implicit subject: WfdSubject): Future[Table] = {
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
  override def search(partialCallSign: String)(implicit subject: WfdSubject): Future[Seq[CallSignId]] = {
    val ucCallSign = partialCallSign.toUpperCase()
    logDocumentCollection.find(regex("_id", s"""$ucCallSign"""))
      .projection(callSignIdInclude)
      .sort(ascending("_id"))
      .toFuture()
      .map { s =>
        s.map(CallSignId(_))
      }

  }

  override def recent()(implicit subject: WfdSubject): Future[Seq[CallSignId]] = {
    logDocumentCollection.find()
      .projection(callSignIdInclude)
      .sort(descending("_id"))
      .limit(recentLimit)
      .toFuture()
      .map { s =>
        s.map(CallSignId(_))
      }

  }

  override def stationCount()(implicit subject: WfdSubject): Int = {
    logDocumentCollection.countDocuments().map(_.toInt).results().head
  }

  val scoringDatabase: MongoDatabase = mongoClient.getDatabase("wfdscoring").withCodecRegistry(codecRegistry).withWriteConcern(WriteConcern.MAJORITY)
  private val scoresCollection: MongoCollection[ScoreRecord] = scoringDatabase.getCollection("scores")


  override def dropScoringDb()(implicit subject: WfdSubject): Unit = {
    scoringDatabase.drop().results()
  }

  override def putScore(scoreRecord: ScoreRecord)(implicit subject: WfdSubject): Unit = {
    scoresCollection.insertOne(scoreRecord).results()
  }

  override def putScores(ranked: Seq[ScoreRecord])(implicit subject: WfdSubject): Unit = {
    scoresCollection.insertMany(ranked).results()
  }

  override def getScores(scoreFilter: ScoreFilter)(implicit subject: WfdSubject): Future[Seq[ScoreRecord]] = {
    import ScoreFilter._
    /**
     * mongodb filter
     */
    import com.mongodb.client.model.Filters._

    val category = scoreFilter.category.getOrElse("")
    (if (category != chooseCategory) {
      scoresCollection.find(equal("callCatSect.category", category))
    } else {
      val section = scoreFilter.section.getOrElse("")
      if (section != chooseSection) {
        scoresCollection.find(equal("callCatSect.arrlSection", section))
      } else {
        scoresCollection.find()
      }
    }).sort(ascending("callCatSect.callSign")).toFuture()
  }

}

object DB {
  private val customCodecs: CodecRegistry = fromProviders(
    // These tell the Scala MongoDB driver which case classes we will be using.
    // This lets Mongo automatically map between scala case classes and mongos BSON document objects.
    classOf[LogInstance],
    classOf[Categories],
    classOf[CallCatSect],
    classOf[StationLog],
    classOf[Exchange],
    classOf[Qso],
    classOf[Qso],
    classOf[MatchedQso],
    classOf[Agg],
    classOf[ModeCount],
    classOf[BandCount],
    classOf[ModeBand],
    classOf[SoapBoxAward],
    classOf[SoapBoxesResult],
    classOf[ScoreRecord],
    classOf[QsoResult],
    classOf[ScoringResult],
  )
  val codecRegistry: CodecRegistry = fromRegistries(customCodecs,
    DEFAULT_CODEC_REGISTRY)

}
