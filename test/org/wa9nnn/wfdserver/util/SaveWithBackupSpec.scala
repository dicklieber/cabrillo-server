package org.wa9nnn.wfdserver.util

import java.nio.file.Files

import org.specs2.mutable.Specification

class SaveWithBackupSpec extends Specification {

  "SaveWithBackup" >> {
    val data1 = "hello1"
    val data2 = "hello2"
    val data3 = "hello3"
    val dir = Files.createTempDirectory("savebackup")
    val d1 = Files.createDirectories(dir)
   val path =  dir.resolve("1")
    "new" >> {

      SaveWithBackup(path, data1)
      SaveWithBackup(path, data2)
      SaveWithBackup(path, data3)
      Files.list(dir).count() must beEqualTo (2)
    }

  }
}
