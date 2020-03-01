package com.wa9nnn.wfdserver.model

import org.specs2.mutable.Specification

import scala.io.{BufferedSource, Source}
import scala.util.Using

class CallSignSpecs extends Specification {


  "CallSign" >> {
    "apply" >> {
      var count = 0

      val r = Using(Source.fromResource("ManyCallsigns.txt")) { bs: BufferedSource =>
        bs.getLines.map { c =>
          count = count + 1
          CallSign(_)
        }
      }
      val generatedCount = r.get.length
      count must beEqualTo (generatedCount)

      }

    }
