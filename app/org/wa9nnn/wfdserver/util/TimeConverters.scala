package org.wa9nnn.wfdserver.util

import java.time.format.DateTimeFormatter
import java.time.{Duration, Instant, ZoneId, ZonedDateTime}
import java.util.TimeZone

import org.wa9nnn.wfdserver.DurationFormat

import scala.language.implicitConversions

object TimeConverters {

  def nanoToSecond(nanoseconds: Double): Double = nanoseconds / 1000000000.0

  val fmt: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm z")

  /**
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


  @scala.inline
  implicit def durationToString(duration: Duration): String = {
    DurationFormat(duration)
  }

  private val _fileStamp = DateTimeFormatter.ofPattern("YYYMMddHHmmssz")

  def fileStamp(in: Instant = Instant.now()): String = {
    _fileStamp.format(ZonedDateTime.ofInstant(in, ZoneId.of("UTC")))
  }

  def instantDisplayUTCCST(instant: Instant): String = {
   val sUtc =  fmt.format(ZonedDateTime.ofInstant(instant, TimeZone.getTimeZone("UTC").toZoneId))
   val scst =  fmt.format(ZonedDateTime.ofInstant(instant, TimeZone.getTimeZone("CST").toZoneId))

    s"$sUtc ($scst)"
  }

}
