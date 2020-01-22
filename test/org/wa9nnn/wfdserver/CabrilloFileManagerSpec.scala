package org.wa9nnn.wfdserver

import java.nio.file.{Files, NoSuchFileException, Path}

import org.apache.commons.io.FileUtils
import org.specs2.execute.{AsResult, Result}
import org.specs2.matcher.DataTables
import org.specs2.mutable.Specification
import org.specs2.specification.ForEach
import org.wa9nnn.wfdserver.util.FileInfo

trait CabrilloFileManagerContext extends ForEach[CabrilloFileManager] {
  def foreach[R:AsResult](r: CabrilloFileManager => R): Result = {
    val cabrilloManager = new CabrilloFileManager(Files.createTempDirectory("FileSaverSpec").toString)
    val fileSaveDirectory: Path = cabrilloManager.fileSaveDirectory

    val result = AsResult(r(cabrilloManager))

    FileUtils.deleteDirectory(fileSaveDirectory.toFile)
    result
  }
}

class CabrilloFileManagerSpec extends Specification with DataTables with CabrilloFileManagerContext {
  val fileBytes: Array[Byte] = "Hello Winter Field Day".getBytes
  val longCallSign = "WA9NNN"
  val shortCallSign = "k9V"
  val oddCallSign = "N9V"
  val bouvetIsland = "3Y/B"


  "CabrilloFileManager" >> {
    "happy path" >> { cfm: CabrilloFileManager =>
      val fileInfo: FileInfo = cfm.save(fileBytes, longCallSign)
      val backAgain = cfm.read(fileInfo.key)
      backAgain must beSuccessfulTry(fileBytes)

    }

    "find read" >> { cfm: CabrilloFileManager =>
      val savePath1 = cfm.save(fileBytes, longCallSign)
      Thread.sleep(250)
      val savePath2 = cfm.save(fileBytes, longCallSign)
      savePath1.stamp must be < savePath2.stamp
      val paths: Seq[FileInfo] = cfm.find(longCallSign)
      paths must haveLength(2)

      cfm.read(paths.head.key).get must beEqualTo(fileBytes)
      cfm.read(paths(1).key).get must beEqualTo(fileBytes)
    }
    "find missing" >> { cfm: CabrilloFileManager =>
      cfm.find("K2ORS") must beEmpty
    }

    "rogue read" >> {
      "just missing" >> { cfm: CabrilloFileManager =>
        cfm.read("dd/pppp-123.cbr") must beAFailedTry[Array[Byte]].withThrowable[NoSuchFileException]
      }
    }
    "illformed" >> { cfm: CabrilloFileManager =>
      cfm.read("crap") must beAFailedTry[Array[Byte]].withThrowable[MatchError]
    }


    "combination" >> { cfm: CabrilloFileManager =>
      "callSign" | "directory" |
        "3Y/B" !! "3Y" |
        "Y/" !! "Y" |
        "Y" !! "Y" |
        "k9V" !! "K9V" |
        "WA9NNN" !! "WA9" |
        "W1AW" !! "W1A" |> { (callSign: String, subDirectory: String) =>
        val fileInfo = cfm.save(fileBytes, callSign)
        val subDir: String = fileInfo.relativePath.getParent.getFileName.toString
        subDir must beEqualTo(subDirectory)
      }
    }
  }

}
