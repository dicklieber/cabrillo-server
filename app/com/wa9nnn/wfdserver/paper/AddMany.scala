
package com.wa9nnn.wfdserver.paper

import java.nio.file.{Files, Paths}
import java.security.SecureRandom

import com.github.racc.tscg.TypesafeConfig
import com.wa9nnn.wfdserver.model.CallSign
import javax.inject.{Inject, Singleton}

import scala.io.BufferedSource
import scala.util.Using

@Singleton
class AddMany @Inject()(@TypesafeConfig("wfd.addManyFile") addManyFile: String) {
  private val path = Paths.get(addManyFile)

  val enabled: Boolean = Files.isReadable(path)

  def apply(howMany: Int)(f: CallSign => Unit): Int = {
    var count = 0
    Using {
      scala.io.Source.fromFile("/Users/dlieber/dev/ham/wfdcheck/conf/SampleCallSigns.txt")
    } { bs: BufferedSource =>
      bs.getLines.foreach { c: String =>
        f(CallSign(c))
        count += 1
        if (count >= howMany) {
          throw new Exception
        }
      }
    }
    count
  }

}

object ManyFreqs {
 private  val random = new SecureRandom()

  def next: String = {
    bandsAndFreq(random.nextInt(bandsAndFreq.length - 1))
  }

  private val bandsAndFreq = Seq(
    "160M",
    "80M",
    "40M",
    "20M",
    "15M",
    "10M",
    "6M",
    "4M",
    "2M",
    "50",
    "70",
    "144",
    "222",
    "432",
    "902",
    "1.2G",
    "2.3G",
    "3.4G",
    "5.7G",
    "10G",
    "24G",
    "47G",
    "75G",
    "123G",
    "134G",
    "241G",
    "1850",
    "3600",
    "7125",
    "21000",
    "285000"
  )


}