
package org.wa9nnn.wfdserver.recaptcha

import java.time.Instant

case class RecaptchaResult(success:Boolean, challenge_ts:Instant, hostname:String, score:Float, action:String )
