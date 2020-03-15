package com.wa9nnn.wfdserver.paper

import java.nio.file.{Files, Path}
import java.time.{Instant, LocalDateTime}

import com.wa9nnn.wfdserver.auth.WfdSubject
import com.wa9nnn.wfdserver.model.{CallSign, PaperLogQso}
import org.apache.commons.io.FileUtils
import org.specs2.execute.{AsResult, Result}
import org.specs2.mutable.Specification
import org.specs2.specification.ForEach

import scala.jdk.StreamConverters._

trait PaperLogDaoContext extends ForEach[SessionDao] {
  val callSign = "WM9W"
  val wfdSubject = WfdSubject("testor")
  val paperLogHeader = new PaperLogHeader(callSign = callSign,
    club = "The 220 MHz Guys",
    name = "dick lieber",
    email = "dick@u505.com",
    address = "148 Bonnie Meadow Road",
    city = "New Rochelle",
    stateProvince = "NY",
    postalCode = "10583",
    category = "1H",
    section = "NLI",
    noMainPower = true,
    txPower = TxPower.low,
    outdoors = true,
    awayFromHome = true,
    satellite = true
  )

  def foreach[R: AsResult](r: SessionDao => R): Result = {
    val paperLogDao = new SessionDao(callSign, Files.createTempDirectory("PaperLogDaoSpec")) (wfdSubject)
    val paperLogDir: Path = paperLogDao.ourDir
println(s"paperLogDir: $paperLogDir")
    val result = AsResult(r(paperLogDao))

    FileUtils.deleteDirectory(paperLogDir.toFile)
    result
  }
}

class PaperLogDaoSpec extends Specification with PaperLogDaoContext {
  val qsoStamp = LocalDateTime.now()

  val qso = PaperLogQso(freq = "7.123", mode = "CW", date= qsoStamp.toLocalDate, time=qsoStamp.toLocalTime,
    theirCall = "W1AW", category =  "1I", section = "CT",
    callSign = CallSign("WA9NNN"))

  "PaperLogDao" >> {
    "start" >> { paperLogDao: SessionDao =>
      val paperLog = paperLogDao.paperLog
      paperLog.qsos must beEmpty
      paperLog.paperLogHeader must beEqualTo(PaperLogHeader(callSign = callSign))
      paperLog.paperLogDetails.lastUpdate.isBefore(Instant.now)
      Files.list(paperLogDao.ourDir).toScala(Iterator).toSeq must length(1)
    }
    "add a header" >> { paperLogDao: SessionDao =>
      paperLogDao.saveHeader(paperLogHeader)
      val paperLog = paperLogDao.paperLog
      paperLog.paperLogDetails.lastUpdate.isBefore(Instant.now)
      paperLog.qsos must beEmpty
      paperLog.paperLogHeader must beEqualTo(paperLogHeader)
    }
    "with header and a qso" >> { paperLogDao: SessionDao =>
      paperLogDao.saveHeader(paperLogHeader)
      paperLogDao.addQso(qso)
      val paperLog = paperLogDao.paperLog
      paperLog.paperLogDetails.lastUpdate.isBefore(Instant.now)
      paperLog.paperLogHeader must beEqualTo(paperLogHeader)
      paperLog.qsos must haveLength(1)
      paperLog.qsos.head must beEqualTo(qso.withIndex(0))

      val filesInDir = Files.list(paperLogDao.ourDir).toScala(Iterator).toSeq.map { f: Path =>
        f.getFileName.toString
      }

      filesInDir must contain("qsos.txt")
      filesInDir must contain("user.txt")
      filesInDir must contain("header.json")
      filesInDir must haveLength(3)
    }

    "multiple QSO Lines">> { paperLogDao: SessionDao =>
      paperLogDao.addQso(qso)
      val qso1 = qso.copy(mode = "PH")
      paperLogDao.addQso(qso1)
      val paperLog = paperLogDao.paperLog
      val qsos = paperLog.qsos
      qsos.head must beEqualTo (qso.withIndex(0))
      qsos(1) must beEqualTo (qso1.withIndex(1))
      qsos must haveLength(2)
    }
  }

}
