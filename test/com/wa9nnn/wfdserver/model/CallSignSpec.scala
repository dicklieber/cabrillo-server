package com.wa9nnn.wfdserver.model

import org.specs2.mutable.Specification

import scala.io.{BufferedSource, Source}
import scala.util.{Try, Using}

class CallSignSpec extends Specification {


  "CallSign" >> {
    "happy" >> {
      CallSign("WA9NNN").toString must beEqualTo ("WA9NNN")
    }
    "lower case" >> {
      CallSign("wa9nnn").toString must beEqualTo ("WA9NNN")
    }
   "isValid" >> {
     "ok" >> {
       CallSign("WA9NNN").valid()
       ok
     }
     "bad" >> {
       CallSign("XX").valid() must throwAn[IllegalArgumentException]
     }
    }

    "many good" >> {
      var count = 0
      var callSigns: Seq[CallSign] = Seq.empty
     val r: Try[Unit] =  Using(Source.fromResource("ManyCallsigns.txt")) { bs: BufferedSource =>
        bs.getLines.foreach { c =>
          count = count + 1
          callSigns = CallSign(c) +: (callSigns)
        }
      }
      r must beSuccessfulTry
      val generatedCount = callSigns.length
      count must beEqualTo(generatedCount)
      ok

    }

    "fileSafe" >> {
      CallSign("WA9nnn").fileSafe must beEqualTo ("WA9NNN")
      CallSign("WA9nnn/6").fileSafe must beEqualTo ("WA9NNN6")
    }

    "implicit option" >> {
      val optCs:Option[CallSign] = CallSign("WA9NNN")
      optCs must beSome[CallSign]
    }

    "Empty fail" >> {
      CallSign("") must throwAn[IllegalArgumentException]
    }

    "compare" >> {
      CallSign.empty must not equalTo(CallSign("WA9NNN"))
    }
  }
}
