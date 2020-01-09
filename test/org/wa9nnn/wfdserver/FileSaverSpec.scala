package org.wa9nnn.wfdserver

import java.nio.file.{Files, Path}

import org.specs2.matcher.DataTables
import org.specs2.mutable.Specification
import org.wa9nnn.wfdserver.scoring.{SoapBoxAward, SoapBoxParser}

class FileSaverSpec extends Specification with DataTables {

  val fileSaver = new FileSaver(Files.createTempDirectory("FileSaverSpec").toString)
  val fileBytes: Array[Byte] = "Hello Winter Field Day".getBytes
  val longCallsign = "WA9NNN"
  val shortCallsign = "k9V"
  val oddCallsign = "N9V"
  val bouvetIsland = "3Y/B"

  "FileSaver" >> {
    "happy path" >> {
      val savePath = fileSaver(fileBytes, longCallsign)
      val filename = savePath.getFileName.toString
      filename must startWith("WA9NNN-")
      val subDir = savePath.getParent.getFileName.toString
      subDir must beEqualTo("WA9")
      val backAgain = Files.readAllBytes(savePath)
      backAgain must beEqualTo(fileBytes)
    }

    "combination" >> {
      "callSign" | "directory" |
        "3Y/B" !! "3Y" |
        "Y/" !! "Y" |
        "Y" !! "Y" |
        "k9V" !! "K9V" |
        "WA9NNN" !! "WA9" |
        "W1AW" !! "W1A" |> { (callSign: String, subDirectory: String) =>
        val savePath = fileSaver(fileBytes, callSign)
        val subDir = savePath.getParent.getFileName.toString
        subDir must beEqualTo(subDirectory)
      }
    }
  }
}
