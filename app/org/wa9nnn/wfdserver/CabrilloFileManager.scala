
package org.wa9nnn.wfdserver

import java.nio.file.{Files, Path, Paths}

import com.github.racc.tscg.TypesafeConfig
import javax.inject.{Inject, Singleton}
import org.wa9nnn.wfdserver.htmlTable.{Header, Table}
import org.wa9nnn.wfdserver.util.{FileInfo, JsonLogging}

import scala.jdk.CollectionConverters._
import scala.util.{Try, Using}

/**
 * Manages archive of uploaded Cabrillo files.
 *
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
  setLoggerName("cabrillo")

  implicit val fileSaveDirectory: Path = Paths.get(carrilloDirectory)

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
   * @return details of save.
   */
  def save(bytes: Array[Byte], callSign: String): FileInfo = {

    val fileInfo = FileInfo(callSign)
    val path = fileInfo.path(fileSaveDirectory)
    Files.createDirectories(path.getParent)

    Using(Files.newOutputStream(path)) { writer =>
      writer.write(bytes)
//      logJson("Save").++(
//        "callSign" -> callSign,
//        "file" -> path.toFile.toPath,
//        "size" -> bytes.length)
//        .info()
    }
    fileInfo
  }

  /**
   *
   * @param callSign of interest
   * @return paths relative to the fileSaveDirectory.
   */
  def find(callSign: String): Seq[FileInfo] = {
    val subDir = fileSaveDirectory.resolve(FileInfo.subDir(callSign))

    if (Files.exists(subDir)) {
      val callSignWithDash = callSign + "-"
      Files.list(subDir)
        .iterator
        .asScala
        .filter {
          _.getFileName.toString.startsWith(callSignWithDash)
        }
        .map { p =>
          FileInfo(p)
        }
        .toSeq
    } else {
      Seq.empty
    }
  }

  /**
   *
   * @param callSign of interest.
   * @return table of all the files saved for this callsign with links to download them.
   */
  def table(callSign: String): Table = {
    Table(Header("Cabrillo Files", "CallSign", "Stamp"),
      find(callSign)
        .sorted
        .map(_.toRow))
      .withCssClass("resultTable")
  }

  /**
   *
   * @param key as returned FileInfo.key.
   * @return bytes of file or an exception.
   */
  def read(key: String): Try[Array[Byte]] = {
    Try {
      val fileInfo = FileInfo.fromKey(key)
      Files.readAllBytes(fileInfo.path(fileSaveDirectory))
    }
  }

}
