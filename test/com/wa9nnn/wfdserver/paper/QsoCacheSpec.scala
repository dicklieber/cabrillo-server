package com.wa9nnn.wfdserver.paper

import java.nio.file.Files

import com.wa9nnn.wfdserver.auth.WfdSubject
import com.wa9nnn.wfdserver.model.PaperLogQso
import com.wa9nnn.wfdserver.util.Page
import org.apache.commons.io.FileUtils
import org.specs2.execute.{AsResult, Result}
import org.specs2.mutable.Specification
import org.specs2.specification.ForEach

trait QsoCacheContext extends ForEach[QsoCache] {
  val callSign = "WM9W"
  private implicit val wfdSubject = WfdSubject("testor")
  val fillSize = 100

  def foreach[R: AsResult](r: QsoCache => R): Result = {
    val ourdir = Files.createTempDirectory("PaperLogDaoSpec")
    val qsoCache = new QsoCache(ourdir)
    val result = AsResult(r(qsoCache))
    FileUtils.deleteDirectory(ourdir.toFile)
    result
  }
}

class QsoCacheSpec extends Specification with QsoCacheContext {
  "QsoCache" >> {
    "initial empty" in { qsoCache: QsoCache =>
      qsoCache.all must beEmpty
      qsoCache.size must beEqualTo(0)
      ok
    }
    "add one" >> { qsoCache: QsoCache =>
      val qso = PaperLogQso(callSign = callSign, theirCall = "W1AW")
      qsoCache.upsert(qso)
      qsoCache.size must beEqualTo(1)
      qsoCache.all.head must beEqualTo(qso.withIndex(0))
    }
    "index" >> { qsoCache: QsoCache =>
      val qso = PaperLogQso(callSign = callSign, theirCall = "W1AW")
      qsoCache.upsert(qso)
      qsoCache.upsert(qso)
      qsoCache.upsert(qso)
      val seq = qsoCache.all
      seq.head.index must beEqualTo(0)
      seq(1).index must beEqualTo(1)
      seq(2).index must beEqualTo(2)
    }
    "get" >> { qsoCache: QsoCache =>
      val qso = PaperLogQso(freq = "1", callSign = callSign, theirCall = "W1AW")
      qsoCache.upsert(qso)
      qsoCache.upsert(qso.copy(freq = "2"))
      val qso3 = qso.copy(freq = "3")
      qsoCache.upsert(qso3)

      qsoCache.get(2).get.freq must beEqualTo("3")
    }

    "update" >> { implicit qsoCache: QsoCache =>
      fill()
      val sizeBefore = qsoCache.size
      val qso = qsoCache.get(3).getOrElse(throw new IllegalStateException("cant get (3) to edit!"))
      val newFreq = "Edited"
      val edited = qso.copy(freq = newFreq)
      qsoCache.upsert(edited)
      qsoCache.size must beEqualTo (sizeBefore)
      val backAgain = qsoCache.get(qso.index).get
      backAgain must beEqualTo (edited)
      backAgain.freq must beEqualTo (newFreq)
    }
    "paging" >> {
      "all" >> { implicit qsoCache: QsoCache =>
        fill()
        val qsos = qsoCache.page(Page.all)

        qsos must haveSize(fillSize)
      }
      "last page multipage" >> { implicit qsoCache: QsoCache =>
        fill()
        val lastPage = qsoCache.page(Page.last)
        lastPage must haveSize(25)
        lastPage.head.freq must beEqualTo("75")
        lastPage.head.index must beEqualTo(75)
        lastPage.last.freq must beEqualTo("99")
        lastPage.last.index must beEqualTo(99)
      }
      "page 0" >> { implicit qsoCache: QsoCache =>
        fill()
        val page = qsoCache.page(Page(0))
        page must haveSize(25)
        page.head.freq must beEqualTo("0")
        page.head.index must beEqualTo(0)
        page.last.freq must beEqualTo("24")
        page.last.index must beEqualTo(24)
      }
      "page 1" >> { implicit qsoCache: QsoCache =>
        fill()
        val page = qsoCache.page(Page(1))
        page must haveSize(25)
        page.head.freq must beEqualTo("25")
        page.head.index must beEqualTo(25)
        page.last.freq must beEqualTo("49")
        page.last.index must beEqualTo(49)
      }
      "after data" >> { implicit qsoCache: QsoCache =>
        fill()
        val page = qsoCache.page(Page(10))
        page must beEmpty
      }
      "short end" >> { implicit qsoCache: QsoCache =>
        fill()
        val page = qsoCache.page(Page(2, 35))
        page must haveSize(30)
        page.head.freq must beEqualTo("70")
        page.head.index must beEqualTo(70)
        page.last.freq must beEqualTo("99")
        page.last.index must beEqualTo(99)
      }
    }
  }

  def fill(n: Int = fillSize)(implicit qsoCache: QsoCache): Unit = {
    for (i <- 0 until n) {
      val qso = PaperLogQso(freq = i.toString, callSign = callSign, theirCall = "W1AW")
      qsoCache.upsert(qso)
    }
    assert(qsoCache.size == n)
  }
}
