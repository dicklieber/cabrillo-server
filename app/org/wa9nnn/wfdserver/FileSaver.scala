
package org.wa9nnn.wfdserver

import java.nio.file.{Files, Path, Paths}
import java.util.UUID

import com.typesafe.config.Config
import javax.inject.{Inject, Singleton}
import org.wa9nnn.wfdserver.util.JsonLogging

import scala.util.Using

/**
 * Saves a file, seq of strings to a file.
 *
 * @param config  containing wfd.saveCabrilloDirectory
 */
@Singleton
class FileSaver @Inject()(config: Config) extends JsonLogging {
  val fileSaveDirectory: Path = Paths.get(config.getString("wfd.saveCabrilloDirectory"))
  try {
    Files.createDirectories(fileSaveDirectory)
  } catch {
    case e: Exception =>
      logger.error(s"Creating ${fileSaveDirectory.toAbsolutePath.toString}  Can't continue.", e)
      System.exit(1)
  }
  if (!Files.isWritable(fileSaveDirectory)) {
    logger.error(s"${fileSaveDirectory.toAbsolutePath.toString} is not writable! Can't continue.")
    System.exit(1)
  }


  def apply(bytes: Array[Byte], email: String): Path = {
    val uuid = UUID.randomUUID().toString
    Files.createDirectories(fileSaveDirectory)

    val filePath: Path = fileSaveDirectory.resolve(s"$email-$uuid")
    Using(Files.newOutputStream(filePath)) { writer =>
      writer.write(bytes)
    }
    filePath
  }
}
