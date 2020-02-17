
package com.wa9nnn.wfdserver.util

import java.io.{File, PrintWriter}
import java.nio.file.{Files, Path, Paths, StandardCopyOption}

/**
 * Handy code inspired from https://rosettacode.org/wiki/Make_a_backup_file#Scala
 */
object SaveWithBackup extends JsonLogging {

  def apply(newFile: Path, data: String): Unit = {
    val backupPath = newFile.resolveSibling(newFile.getFileName.toString + ".backup")
    if (Files.exists(newFile))
      Files.move(newFile, backupPath, StandardCopyOption.REPLACE_EXISTING)
    Files.write(newFile, data.getBytes)

    assert(Files.exists(newFile), "Didn't save file!")
  }

}
