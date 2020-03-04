package com.wa9nnn.wfdserver.paper

import com.wa9nnn.wfdserver.model.CallSign
import org.specs2.mutable.Specification

class AddManySpecs extends Specification {

  "enabled" >> {
    "ok" >> {
      new AddMany(getClass.getResource("/resources/ManyCallsigns.txt").getFile).enabled must beTrue
    }
    "no file" >> {
      new AddMany("noFile").enabled must beFalse
    }
  }

  "apply" in {
    val addMany = new AddMany(getClass.getResource("/resources/ManyCallsigns.txt").getFile)
    val csCollector = Seq.newBuilder[CallSign]
    addMany(5) {
      csCollector += (_)
    }
    val result = csCollector.result()
    result must haveLength(5)
    result.head.cs must beEqualTo ("2O0BSE")
  }

}
