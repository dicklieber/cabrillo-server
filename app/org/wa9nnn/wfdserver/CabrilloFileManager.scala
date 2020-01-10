
package org.wa9nnn.wfdserver

import java.nio.file.{Files, Path, Paths}
import java.util.UUID

import com.github.racc.tscg.TypesafeConfig
import javax.inject.{Inject, Singleton}
import org.wa9nnn.wfdserver.util.JsonLogging

import scala.jdk.CollectionConverters._
import scala.util.{Try, Using}

/**
 * Saves a file, seq of strings to a file.
 * This is essential for operation.
 * If the "wfd.saveCabrilloDirectory" in application.conf cannot be created or is not writable,
 * then the server will be abruptly terminated, System.exit(1)!
 *
 * Files live in sub directories of the fileSaveDirectory. Files are sharded into sub directories named with the 1st 3 chars of the callSign.
 *
 * @param carrilloDirectory  where to save raw cabrillo files at.
 */
@Singleton
class CabrilloFileManager @Inject()(@TypesafeConfig("wfd.saveCabrilloDirectory") carrilloDirectory: String) extends JsonLogging {

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
    val subDirectory = subdir(callSign)

    Files.createDirectories(subDirectory)

    val filePath: Path = subDirectory.resolve(s"$callSign-$uuid")
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

  /**
   * handles sharding based on 1st 3 or so chars of the callSign.
   * Removes slashes and may return less than 3 if that aren't enough.
   * @param callSign of interest.
   * @return
   */
  private def subdir(callSign: String) = {
    val ucCallSign = callSign.toUpperCase
    val safeChars = ucCallSign.replaceAll("/", "")
    fileSaveDirectory.resolve(safeChars.take(3))
  }

  /**
   *
   * @param callSign of interest
   * @return paths relative to the fileSaveDirectory.
   */
  def find(callSign: String): Seq[Path] = {
    val subDir = subdir(callSign)
    if(Files.exists(subDir)) {
      val callSignWithDash = callSign + "-"
      Files.list(subDir)
        .iterator
        .asScala
        .filter {
          _.getFileName.toString.startsWith(callSignWithDash)
        }
        .map {
          fileSaveDirectory.relativize
        }
        .toSeq
    }else{
      Seq.empty
    }
  }

  /**
   *
   * @param relativePath as returned by paths.
   * @return bytes of file or an exception.
   */
  def read(relativePath: Path): Try[Array[Byte]] = {
    Try(

      Files.readAllBytes(fileSaveDirectory.resolve(relativePath))
    )
  }

}
