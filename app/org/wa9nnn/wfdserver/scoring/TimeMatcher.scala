
package org.wa9nnn.wfdserver.scoring

import java.time.{Duration, Instant}

import com.github.racc.tscg.TypesafeConfig
import javax.inject.{Inject, Singleton}

/**
 * determine if two [[Instant]]s are close enough together.
 * @param duration must be within this duration.
 */
@Singleton
class TimeMatcher @Inject()(@TypesafeConfig("wfd.scoring.timeMatchDuration") duration: Duration) {
  private val seconds = duration.getSeconds

  def apply(t1: Instant, t2: Instant): Boolean = {
    Duration.between(t1, t2).getSeconds < seconds
  }
}
