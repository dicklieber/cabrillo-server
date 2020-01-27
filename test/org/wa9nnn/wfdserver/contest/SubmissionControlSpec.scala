package org.wa9nnn.wfdserver.contest

import java.nio.file.{Files, Path}
import java.time.{Clock, ZoneId}

import org.specs2.mutable.Specification
import org.wa9nnn.wfdserver.contest.SubmissionConfig.default

class SubmissionControlSpec extends Specification {
  def newDao(n:Int, clock:Clock = Clock.systemUTC()): SubmissionControlDao = {
    val dir = Files.createTempDirectory("SubmissionControlSpec")
    val path: Path = dir.resolveSibling(s"subcontrol$n")
    println(s"path: $path")
    new SubmissionControlDao(path, clock)
  }

  "SubmissionControl" >> {

    "get default" >> {
      val dao = newDao(1)
      val sc: SubmissionConfig = dao.get
      sc.times must beEqualTo (default.times)
    }

    "put" >> {
      val dao = newDao(2)
      val submisionsConfig = dao.get
      val updated = submisionsConfig.copy(duringMessage = Message("changed header", "changed body"))

      dao.put(updated)

      val backAgain = dao.get
      backAgain must beEqualTo(updated)
    }

//    "apply before" >> {
//      val beforeInstant = default.times.submissionBegin.minusSeconds(100000)
//      val state: CurrentSubmissionState = newDao(3, Clock.fixed(beforeInstant, ZoneId.of("UTC"))).current()
//      state.message must beEqualTo (default.before.message)
//      state.submissionsAllowed must beFalse
//    }

  }
}
