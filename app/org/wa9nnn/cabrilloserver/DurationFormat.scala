
package org.wa9nnn.cabrilloserver


import java.time.Duration

import scala.language.implicitConversions

/**
 * Converts a [[java.time.Duration]] to nice for carbon-based units.
 */
object DurationFormat {

  val ONE_SECOND = 1000L
  val ONE_MINUTE = 60000L
  val ONE_HOUR = 3600000L
  val ONE_DAY = 86400000L


  /**
   *
   * @param duration how long
   * @return  .e.g. "59 min 2 sec" or "999 ms"
   */
  implicit def apply(duration: Duration): String = {
    duration.toMillis match {
      case ms if ms < ONE_SECOND => f"${duration.toMillis} ms"
      case ms if ms == ONE_SECOND => "1 sec"
      case ms if ms < ONE_MINUTE =>
        val remaininMs = ms % ONE_SECOND
        f"${ms / ONE_SECOND}%d sec $remaininMs%d ms"
      case ms if ms == ONE_MINUTE => "1 min"
      case ms if ms < ONE_HOUR =>
        val minutes = duration.toMinutes
        val remaininSecs = (ms - minutes * ONE_MINUTE) / ONE_SECOND
        f"$minutes%d min $remaininSecs%d sec"
      case ms if ms < ONE_DAY =>
        val hours = ms / ONE_HOUR
        val minutes = (ms - hours * ONE_HOUR) / ONE_MINUTE
        f"$hours%d hours $minutes%d min"
      case ms =>
        val days = ms / ONE_DAY
        val hours = (ms - days * ONE_DAY) / ONE_HOUR
        val mins = (ms - (hours * ONE_HOUR + days * ONE_DAY)) / ONE_MINUTE
        if (mins == 0) {
          f"$days%d day $hours%d hour"
        } else {
          f"$days%d day $hours%d hour $mins%d min"
        }
    }
  }
}

