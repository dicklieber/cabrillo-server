package com.wa9nnn.wfdserver.db.mongodb

import org.apache.commons.io.IOUtils
import org.specs2.mutable.Specification
import com.wa9nnn.cabrillo.Cabrillo
import com.wa9nnn.wfdserver.db.LogInstanceAdapter

class LogInstanceAdapterSpec extends Specification {
  implicit def f(file: String): Array[Byte] = IOUtils.resourceToByteArray(file)

  "LogInstanceAdapter" >> {
    val result = Cabrillo("/wfd1.cbr")
    "apply" >> {
      val logInstance = LogInstanceAdapter(result.cabrilloData)
      logInstance.qsoCount must beEqualTo (logInstance.qsos.length)
      logInstance.id must beEqualTo (logInstance._id)
    }

  }
}
