package com.wa9nnn.wfdserver.paper

import java.nio.file.{Files, Path}

import com.wa9nnn.wfdserver.auth.WfdSubject
import org.apache.commons.io.FileUtils
import org.specs2.execute.{AsResult, Result}
import org.specs2.mutable.Specification
import org.specs2.specification.ForEach

trait PaperLogsDaoContext extends ForEach[PaperLogsDao] {
  val callSign = "WM9W"
  implicit val wfdSubject: WfdSubject = WfdSubject("testor")
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
    txPower = TxPower.medium,
    outdoors = true,
    awayFromHome = true,
    satellite = true
  )

  def foreach[R: AsResult](r: PaperLogsDao => R): Result = {
    val paperLogsDao = new PaperLogsDao(Files.createTempDirectory("PaperLogsDaoSpec").toString)
    val paperLogDir: Path = paperLogsDao.directory

    val result = AsResult(r(paperLogsDao))

    FileUtils.deleteDirectory(paperLogDir.toFile)
    result
  }
}

class PaperLogsDaoSpec extends Specification with PaperLogsDaoContext {

  "PaperLogsDaoSpec" >> {
    "start new" >> { paperLogsDao: PaperLogsDao =>
      val t: SessionDao = paperLogsDao.start(callSign)
      ok
    }
    "several" >> { paperLogsDao: PaperLogsDao =>
      paperLogsDao.start("N9VTB")
      paperLogsDao.start("NE9A")
      paperLogsDao.start("KD9BYW")

      val sessions = paperLogsDao.list()
      sessions must haveLength(3)
    }


  }
}
