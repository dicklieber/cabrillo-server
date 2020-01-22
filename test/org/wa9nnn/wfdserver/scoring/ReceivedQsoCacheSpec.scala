package org.wa9nnn.wfdserver.scoring

import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.core.Failed
import org.wa9nnn.wfdserver.CallSignId
import org.wa9nnn.wfdserver.auth.WfdSubject
import org.wa9nnn.wfdserver.db.mongodb.{DB, LoaderTest10}
import org.wa9nnn.wfdserver.model.{Exchange, LogInstance, Qso, QsoKey}
import play.api.libs.json.Json

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class ReceivedQsoCacheSpec extends Specification with Mockito {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
//  new LoaderTest10()
//  val db = new DB(dbName = "wfd-test10")


  implicit val subject: WfdSubject = WfdSubject("test")
  implicit val timeMatcher = new TimeMatcher(java.time.Duration.ofHours(2))
  implicit val exchangeFormat = Json.format[Exchange]
  implicit val qsoDFormat = Json.format[Qso]
  implicit val qsoScoreFormat = Json.format[MatchedQso]
  implicit val stationFormat = Json.format[MatchedQsosforCallSign]

  "ReceivedQsoCache" >> {
    "loadall style" >> {
      val receivedQsoCache = new ReceivedQsoCache()

      Failed
      ok
    }

//    "one processed qso" >> {
//      val receivedQsoCache = new ReceivedQsoCache(db)
//
//      val logInstance: LogInstance = (for {
//        csid <- Await.result[Seq[CallSignId]](db.callSignIds(), 5 seconds).take(1)
//        logInstance <- Await.result[Option[LogInstance]](db.logInstance(csid.entryId), 5 seconds)
//      } yield {
//        logInstance
//      }).toSeq.head
//      val qso = logInstance.qsos.head
//      val mayBeMatchingQso: Option[Qso] = receivedQsoCache(qso)
//      mayBeMatchingQso must beSome[Qso]
//      val matchedQso = mayBeMatchingQso.get
//      matchedQso.receivedKey must beEqualTo(qso.sentKey)
//
//      val qsoPair = MatchedQso(qso, mayBeMatchingQso)
//      println(qsoPair.display())
//
//     try {
//       qsoPair.validate(logInstance.stationLog.callSign)
//     } catch {
//       case e:Exception =>
//         e.printStackTrace()
//     }
//
//      receivedQsoCache.consumedCallSigns must haveLength(1)
//      val consumedCallSign = receivedQsoCache.consumedCallSigns.head
//      consumedCallSign must beEqualTo(qso.r.cs)
//      val wrongQsos = receivedQsoCache.map.values.filterNot { cachedQso => cachedQso.s.cs == consumedCallSign }
//      wrongQsos must haveLength(0)
//    }
//
//    "happy path" in {
//      val receivedQsoCache = new ReceivedQsoCache(db)
//      val r: Seq[MatchedQsosforCallSign] = for {
//        csid <- Await.result[Seq[CallSignId]](db.callSignIds(), 5 seconds)
//        logInstance <- Await.result[Option[LogInstance]](db.logInstance(csid.entryId), 5 seconds)
//      } yield {
//        val qsoScores: Seq[MatchedQso] = logInstance.qsos.map { qso =>
//          MatchedQso(qso, receivedQsoCache(qso))
//        }
//        MatchedQsosforCallSign(logInstance.stationLog.callSign, qsoScores)
//      }
//      receivedQsoCache.consumedCallSigns must haveLength(10)
//      val errors = r.flatMap {f: MatchedQsosforCallSign =>
//        val e: Seq[Throwable] = f.validate
//        if(e.isEmpty){
//          println(e)
//        }
//        e
//      }
////      errors must haveLength(0)
//
//      r.foreach { sqc =>
//        println(sqc.callSign + ":")
//        sqc.qsoPairs.foreach { qsoPair: MatchedQso =>
//          println("\t" + qsoPair.display())
//        }
//      }
//      ok
//
//    }
  }
}

