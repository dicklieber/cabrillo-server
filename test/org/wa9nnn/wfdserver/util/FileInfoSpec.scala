package org.wa9nnn.wfdserver.util

import java.nio.file.Paths
import java.time.{Clock, Instant, ZoneId}

import org.specs2.mutable.Specification

class FileInfoSpec extends Specification {
  private  val fileSaveDir = Paths.get("filSavdir")
  val callSign = "WA9NNN"
  "FileInfoSpec" should {
    "hex/Instant" in {
      val instant = Instant.EPOCH
      val s = FileInfo.instantToHex(instant)
      FileInfo.hexToInstant(s) must beEqualTo(instant)
      s must beEqualTo("0")
    }
    "hex/Instant now" in {
      val instant = Instant.now
      val s = FileInfo.instantToHex(instant)
      FileInfo.hexToInstant(s) must beEqualTo(instant)
    }


    "apply" in {
      val fileInfo = FileInfo(callSign, Clock.fixed(Instant.ofEpochMilli(20000), ZoneId.of("UTC")))
      fileInfo.callSign must beEqualTo(callSign)
      fileInfo.relativePath.toString must beEqualTo("WA9/WA9NNN-4e20.cbr")
      fileInfo.stamp.toEpochMilli must beEqualTo(20000)
    }

    "apply" in {
      val path = Paths.get("filSavdir/WA9/WA9NNN-4e20.cbr")
      val fileInfo = FileInfo(path)
      fileInfo.callSign must beEqualTo(callSign)
      fileInfo.relativePath.toString must beEqualTo("WA9/WA9NNN-4e20.cbr")
      fileInfo.stamp.toString must beEqualTo("1970-01-01T00:00:20Z")
    }

    "round trip via key" >> {
      val fileInfo = FileInfo(callSign, Clock.fixed(Instant.ofEpochMilli(20000), ZoneId.of("UTC")))
      val key = fileInfo.key
      key must beEqualTo("WA9NNN-4e20.cbr")
      val backAgain = FileInfo.fromKey(key)
      backAgain must beEqualTo(fileInfo)
    }
    "path" >> {
      val fileInfo = FileInfo(callSign, Clock.fixed(Instant.ofEpochMilli(20000), ZoneId.of("UTC")))
      fileInfo.path(fileSaveDir).toString must beEqualTo ("filSavdir/WA9/WA9NNN-4e20.cbr")
    }
  }
}
