
package org.wa9nnn.wfdserver

import java.nio.file.{Files, Path, Paths}
import java.util.UUID

import com.github.racc.tscg.TypesafeConfig
import javax.inject.{Inject, Singleton}
import org.wa9nnn.wfdserver.util.JsonLogging

import scala.util.Using

/**
 * Saves a file, seq of strings to a file.
 * This is essential for operation.
 * If the "wfd.saveCabrilloDirectory" in application.conf cannot be created or is not writable,
 * then the server will be abruptly terminated, System.exit(1)!
 *
 * @param carrilloDirectory  where to save fraw cabrillo files at.
 */
@Singleton
class FileSaver @Inject()(@TypesafeConfig("wfd.saveCabrilloDirectory") carrilloDirectory: String) extends JsonLogging {
  val fileSaveDirectory: Path = Paths.get(carrilloDirectory)
  //  val fileSaveDirectory: Path = Paths.get(config.getString("wfd.saveCabrilloDirectory"))
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

  /**
   *
   * @param bytes    body of file to be saved.
   * @param callSign callsign. Base of filename
   * @return actual path where file was saved.
   */
  def apply(bytes: Array[Byte], callSign: String): Path = {
    val uuid = UUID.randomUUID().toString
    val ucCallSign = callSign.toUpperCase
    val safeChars = ucCallSign.replaceAll("/", "")
    val subDirectory = fileSaveDirectory.resolve(safeChars.take(3))

    Files.createDirectories(subDirectory)

    val filePath: Path = subDirectory.resolve(s"$ucCallSign-$uuid")
    Using(Files.newOutputStream(filePath)) { writer =>
      writer.write(bytes)
      logJson("Save").++(
        "callSign" -> callSign,
        "file" -> filePath.toFile.toPath,
        "size" -> bytes.length)
        .info()

    }
    filePath
  }
}
