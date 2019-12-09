
package org.wa9nnn.cabrilloserver

import java.nio.file.{Files, Path}
import java.util.UUID

import scala.io.BufferedSource
import scala.util.Using

class FileSaver(fileSaveDirectory: Path) {
  def apply(bufferedSource: BufferedSource, email: String): Path = {
    val uuid = UUID.randomUUID().toString
    Files.createDirectories(fileSaveDirectory)


    val filePath = fileSaveDirectory.resolve(s"$email-$uuid")
    Using(Files.newBufferedWriter(filePath)) { writer =>
      Using(bufferedSource.bufferedReader()) { reader =>
        val buffer = new Array[Char](1024)

        var count = Integer.MAX_VALUE

        while ( count > 0){
          count = reader.read(buffer)
          writer.write(buffer, 0, count)
        }
      }
    }
    filePath
  }
}
