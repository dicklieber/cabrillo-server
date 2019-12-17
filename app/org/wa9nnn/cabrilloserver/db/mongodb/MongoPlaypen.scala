
package org.wa9nnn.cabrilloserver.db.mongodb

import java.time.{Instant, LocalDateTime}
import java.util.UUID

import org.bson.codecs.configuration.CodecRegistries._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase, _}
import org.wa9nnn.cabrillo.Builder
import org.wa9nnn.cabrillo.parsers.Exchange_WFD
import org.wa9nnn.cabrilloserver.util.JsonLogging

object MongoPlaypen extends JsonLogging{
  def main(args: Array[String]): Unit = {
    val customCodecs = fromProviders(
      classOf[StationLog],
      classOf[QSO],
      classOf[Exchange_WFD],
    )

    //     val javaCodecs = CodecRegistries.fromCodecs(
    //      new LocalDateTimeDateCodec(),
    //      new LocalDateDateCodec(),
    //      new BigDecimalStringCodec())

    val codecRegistry = fromRegistries(customCodecs,
      //      javaCodecs,
      DEFAULT_CODEC_REGISTRY)


    val mongoClient: MongoClient = MongoClient()
    val database: MongoDatabase = mongoClient.getDatabase("wfd-dev").withCodecRegistry(codecRegistry)
    println(database)


    val entryCollection: MongoCollection[StationLog] = database.getCollection("logs")
    val qsoCollection: MongoCollection[QSO] = database.getCollection("qsos")

    val cabrilloData = new Builder()
      .+("CALLSIGN", "WA9NNN")
      .+("EMAIL", "dick@u505.com")
      .toCabrilloData

    val stationLog = StationLog("WM9W", "IL", "3I", Seq("CATEGORY-OPERATOR" -> "MULTI-OP").toMap, Seq("1,500 points for setting up away from home."), Some("dick@u505.com"), "Dick Lieber", 42)



    val value: SingleObservable[Completed] = entryCollection.insertOne(stationLog)
    //    val value: SingleObservable[Completed] = collection.insertOne(doc)

    value.subscribe(new Observer[Completed] {

      override def onNext(result: Completed): Unit = {
        val str = result.toString()

        logJson("Inserted  Log").info()
      }
      override def onError(e: Throwable): Unit =
        logJson("Failed Log").error(e)

      override def onComplete(): Unit =
        logJson("Completed  Log").info()
    })

    val qso = QSO(
      logId = stationLog._id,
      freq = "7.23",
      mode = "DI",
      stamp = Instant.now,
      sent = Exchange_WFD("WM9W", "4H", "IL"),
      received = Exchange_WFD("W1AW", "1O", "CT")
    )
    val resultQso = qsoCollection.insertOne(qso)

    resultQso.subscribe(new Observer[Completed] {

      override def onNext(result: Completed): Unit =
        logJson("Inserted Qso")
          .info()

      override def onError(e: Throwable): Unit =
        logJson("Failed Qso").error(e)

      override def onComplete(): Unit =
        logJson("Completed  Qso").info()
    })


    Thread.sleep(50000)
    println("done")
  }

}
