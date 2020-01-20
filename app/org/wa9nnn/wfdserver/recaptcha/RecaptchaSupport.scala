
package org.wa9nnn.wfdserver.recaptcha

import java.io.DataOutputStream
import java.net.URL

import akka.http.scaladsl.model.HttpResponse
import com.typesafe.config.Config
import javax.inject.{Inject, Singleton}
import javax.net.ssl.HttpsURLConnection
import org.wa9nnn.wfdserver.util.{JsonLogging, LogJson}
import play.api.libs.json.Json

import scala.util.Using

@Singleton
class RecaptchaSupport @Inject()(config: Config) extends JsonLogging {
  private val secretKey: String = config.getString("wfd.recaptcha.secretKey")
  private val minimumScore: Double = config.getDouble("wfd.recaptcha.minimumScore")
  private val recaptchaUrl = new URL(config.getString("wfd.recaptcha.serviceUrl"))

  private implicit val recaptchaResponseFormat = Json.format[RecaptchaResult]

  /**
   *
   * @param recaptchaResponse from recaptcha via javascript interaction with recaptcha
   * @return true if we like this client
   */
  def apply(recaptchaResponse: String): Boolean = {

    try {
      val postParams = s"secret=$secretKey&response=$recaptchaResponse"

      val httpUrlConnection: HttpsURLConnection = recaptchaUrl.openConnection.asInstanceOf[HttpsURLConnection]
      httpUrlConnection.setRequestMethod("POST")
      httpUrlConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
      httpUrlConnection.setDoOutput(true)
      Using(new DataOutputStream(httpUrlConnection.getOutputStream)) { dataOutputStream =>
        dataOutputStream.writeBytes(postParams)
        dataOutputStream.flush()
        dataOutputStream.close()
        val responseCode = httpUrlConnection.getResponseCode
        if (responseCode == 200) {
          logger.debug("Post parameters: " + postParams)
          val recaptchaResult = Using(httpUrlConnection.getInputStream) { inputStream =>
            Json.parse(inputStream).as[RecaptchaResult]
          }
          val result = recaptchaResult.get
          (logJson("recaptcha") :+ (result)).info()
          result.score >= minimumScore
        } else {
          logger.debug(s"recaptcha Response Code: $responseCode")
          true
        }
      }.get

    } catch {
      case e: Exception =>
        logger.error("processing captchatoken", e)
        true // we probably did something wrong, don't penalize user.
    }

  }
}
