package org.wa9nnn.wfdserver.db.mongodb

import com.typesafe.config.ConfigFactory
import org.apache.commons.io.IOUtils
import org.specs2.mutable.Specification
import org.wa9nnn.cabrillo.{Cabrillo, ResultWithData}
import org.wa9nnn.wfdserver.CallSignId
import org.wa9nnn.wfdserver.db.LogInstanceAdapter

import scala.concurrent.duration._
import scala.concurrent.Await

class DBSpec extends Specification {
  implicit def f(file: String): Array[Byte] = IOUtils.resourceToByteArray(file)

  val db = new DB("mongodb://localhost")
  db.database.drop()
  "DBSpec" should {
    val result: ResultWithData = Cabrillo("/wfd1.cbr")
    "apply" >> {
      val logInstance = LogInstanceAdapter(result.cabrilloData)
      db.ingest(logInstance)

      "search happy path" in {
        val callSignIds = Await.result[Seq[CallSignId]](db.search("4"), 1 minute)
        callSignIds must haveLength(1)
        callSignIds.head.callsign must beEqualTo(logInstance.stationLog.callSign)
      }
      "case" in {
        val callSignIds = Await.result[Seq[CallSignId]](db.search("l"), 1 minute)
        callSignIds must haveLength(1)
        callSignIds.head.callsign must beEqualTo(logInstance.stationLog.callSign)
      }
      "no match" in {
        val callSignIds = Await.result[Seq[CallSignId]](db.search("NNN"), 1 minute)
        callSignIds must beEmpty
      }
      "recent" in {
        val callSignIds = Await.result[Seq[CallSignId]](db.recent, 1 minute)
        callSignIds must not be empty // pretty lame test a we onlhy have one
      }

    }
  }
}
