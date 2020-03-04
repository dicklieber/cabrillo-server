
package com.wa9nnn.wfdserver.paper

import java.nio.file.{Files, Paths}

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
        if(count >= howMany){
          throw new Exception
        }
      }
    }
    count
  }

}
