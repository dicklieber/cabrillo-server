package com.wa9nnn.wfdserver.util

import java.sql
import java.time.format.DateTimeFormatter
import java.time.{Duration, Instant, ZoneId, ZonedDateTime}
import java.util.TimeZone

import com.wa9nnn.wfdserver.DurationFormat

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
    fmt.format(ZonedDateTime.ofInstant(instant, ZoneId.of("UTC")))
    //    fmt.format(ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()))
  }

  /**
   * sql date and time suck!  There's's no good way to combine them
   *
   * @param sqlDate stupid java.sql.Date
   * @param sqlTime stupid  java.sql.Time
   * @return the java instant
   */
  def sqlToInstant(sqlDate: sql.Date, sqlTime: sql.Time): Instant = {
    // It seems that the3 getTime vale of sql.Time doesn't round-trip through MySQL

    val time: Long = sqlDate.getTime
    val date: Long = sqlTime.getTime
    val r = if (time == date) {
      Instant.ofEpochMilli(date)
    } else {
      Instant.ofEpochMilli(date + time)
    }
    r
  }

  def instantToSql(instant: Instant): (sql.Date, sql.Time) = {
    val milli = instant.toEpochMilli
    new sql.Date(milli) -> new sql.Time(milli)
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
    val sUtc = fmt.format(ZonedDateTime.ofInstant(instant, TimeZone.getTimeZone("UTC").toZoneId))
    val scst = fmt.format(ZonedDateTime.ofInstant(instant, TimeZone.getTimeZone("CST").toZoneId))

    s"$sUtc ($scst)"
  }

}
