package com.wa9nnn.wfdserver.contest

import java.nio.file.Files

import com.wa9nnn.wfdserver.contest.SubmissionConfig.default
import org.apache.commons.io.FileUtils
import org.specs2.execute.{AsResult, Result}
import org.specs2.mutable.Specification
import org.specs2.specification.ForEach

trait PaperLogDaoContext extends ForEach[SubmissionControlDao] {

  def foreach[R: AsResult](r: SubmissionControlDao => R): Result = {
    val dir = Files.createTempDirectory("SubmissionControlSpec")
    println(s"Create $dir")
    val dao: SubmissionControlDao = new SubmissionControlDao(dir.resolve("controlFile.json"))

    val result = AsResult(r(dao))

    println(s"delete $dir")
    FileUtils.deleteDirectory(dir.toFile)
    result
  }
}

class SubmissionControlSpec extends Specification with PaperLogDaoContext{

  "SubmissionControl" >> {

    "get default" >> {dao:SubmissionControlDao =>
      val sc: SubmissionConfig = dao.get
      sc.times must beEqualTo (default.times)
    }

    "put" >> {dao:SubmissionControlDao =>
      val submisionsConfig = dao.get
      val updated = submisionsConfig.copy(duringMessage = Message("changed header", "changed body"))

      dao.put(updated)

      val backAgain = dao.get
      backAgain must beEqualTo(updated)
    }
  }
}
