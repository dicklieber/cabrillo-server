
package org.wa9nnn.wfdserver.util

import java.nio.file.{Path, Paths}
import java.time.{Clock, Instant}

import controllers.routes
import org.wa9nnn.wfdserver.htmlTable.{Cell, Row, RowSource}

import scala.jdk.CollectionConverters._

/**
 * Details about a Cabrillo file being saved or retrieved.
 *
 * @param callSign     of interest.
 * @param stamp        when it was saved.
 * @param relativePath sub dir and filename.
 */
case class FileInfo(callSign: String, stamp: Instant, relativePath: Path) extends RowSource  with Ordered[FileInfo] {
  /*
  unique id for this saved file.
   */
  def key: String = relativePath.getFileName.toString

  def path(fileSaveDir: Path): Path = fileSaveDir.resolve(relativePath)

  def subDir(fileSaveDir: Path): Path = fileSaveDir.resolve(relativePath.getParent)

  override def toRow: Row = {
    Row(
      Cell(callSign)
        .withUrl(routes.FilesController.download(key).url),
      stamp
    )
  }

  override def compare(that: FileInfo): Int = {
    // Newest at top
    that.stamp compareTo(this.stamp)
  }
}

object FileInfo {
  implicit def instantToHex(instant: Instant): String = instant.toEpochMilli.toHexString

  implicit def hexToInstant(hex: String): Instant = {
    Instant.ofEpochMilli(java.lang.Long.valueOf(hex, 16))
  }

  /**
   * handles sharding based on 1st 3 or so chars of the callSign.
   * Removes slashes and may return less than 3 if that aren't enough.
   *
   * @param callSign of interest.
   * @return
   */
  def subDir(callSign: String): Path = {
    val safeChars = callSign.replaceAll("/", "")
    Paths.get(safeChars.take(3))
  }

  /**
   *
   * @param callSign of interest.
   * @param clock    for unit tests so we hae a deterministic time. Normallly use system clock.
   */
  def apply(callSign: String, clock: Clock = Clock.systemUTC()): FileInfo = {
    val ucCallSign = callSign.toUpperCase //todo where best to do this?
    val now = Instant.now(clock)
    val fileName = s"$ucCallSign-${instantToHex(now)}.cbr"
    val path = subDir(ucCallSign).resolve(fileName)
    new FileInfo(ucCallSign, now, path)
  }

  private val r = """(.+)-(.+).cbr""".r

  /**
   *
   * @param path will just use the last two elements so can be absolute or relative.
   */
  def apply(path: Path): FileInfo = {
    val lastTwo: Seq[Path] = path.iterator().asScala.toSeq.takeRight(2)
    val last = lastTwo.last
    val relativePath = lastTwo.head.resolve(last)
    val r(callSign, hexStamp) = last.toString
    val instant: Instant = hexStamp

    new FileInfo(callSign, instant, relativePath)
  }

  def fromKey(key: String): FileInfo = {
    val r(callSign, hex) = key
    val stamp: Instant = hex
    val path = subDir(callSign).resolve(key)
    new FileInfo(callSign, stamp, path)
  }
}
