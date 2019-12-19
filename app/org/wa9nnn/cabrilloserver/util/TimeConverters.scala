package org.wa9nnn.cabrilloserver.util

import java.time.format.DateTimeFormatter
import java.time.{Duration, Instant, ZoneId, ZonedDateTime}

import org.wa9nnn.cabrilloserver.DurationFormat

import scala.language.implicitConversions

object TimeConverters {

  def nanoToSecond(nanoseconds: Double): Double = nanoseconds / 1000000000.0

  val fmt: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm:ss z")

  /**
    * Note this only works with values from instant.toString, NOT any
    * of the [[java.time.Instant]] toString methods in this object.
    * @param in value from instant.toString
    * @return corresponding Instant
    */
  @scala.inline
  implicit def stringToInstant(in: String): Instant = {
    Instant.parse(in)
  }

  /**
    * Nice format with zone
    */
  @scala.inline
  implicit def instantToString(instant: Instant): String = {
    fmt.format(ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()))
  }

  /**
    * formats time like "07/02/16 14:43:24 CDT (2016-07-02T19:43:24.418Z)"
    * @param instant required
    * @param zoneId if not using default.
    * @return
    */
//  def localAndUtc(instant: Instant, zoneId: ZoneId = ZoneId.systemDefault()): String = {
//    val local = fmt.format(ZonedDateTime.ofInstant(instant, zoneId))
//    s"$local (${instant.toString})"
//  }

  @scala.inline
  implicit def durationToString(duration: Duration): String = {
    DurationFormat(duration)
  }

  private val _fileStamp = DateTimeFormatter.ofPattern("YYYMMddHHmmssz")
  def fileStamp(in: Instant = Instant.now()): String = {
    _fileStamp.format(ZonedDateTime.ofInstant(in, ZoneId.of("UTC")))
  }

}
