package com.wa9nnn.wfdserver.play

import java.nio.file.Paths

import org.specs2.mutable.Specification

class WfdConfigFinderSpec extends Specification {

  "WfdConfigFinder" >> {
      val config = WfdConfigFinder.wfdVarDir
      WfdConfigFinder
      val config2 = WfdConfigFinder.wfdVarDir
      ok

  }
}
