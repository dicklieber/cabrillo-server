package org.wa9nnn.wfdserver

import java.io.IOException
import java.nio.file.{Files, Path, Paths}

import org.specs2.matcher.DataTables
import org.specs2.mutable.Specification


class FileSaverSpec extends Specification with DataTables {
  isolated

  val fileSaver = new CabrilloFileManager(Files.createTempDirectory("FileSaverSpec").toString)
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
        "find read" >> {
          val savePath1 = fileSaver(fileBytes, longCallsign)
          val savePath2 = fileSaver(fileBytes, longCallsign)

          val paths: Seq[Path] = fileSaver.find(longCallsign)
          paths must haveLength(2)

          fileSaver.read(paths.head).get must beEqualTo(fileBytes)
          fileSaver.read(paths(1)).get must beEqualTo(fileBytes)
        }
        "find missing" >> {
          fileSaver.find("K2ORS") must beEmpty
        }

        "rogue read" >> {
          "just mising" >> {
            fileSaver.read(Paths.get("dd/pppp")) must beAFailedTry[Array[Byte]].withThrowable[IOException]
          }
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
