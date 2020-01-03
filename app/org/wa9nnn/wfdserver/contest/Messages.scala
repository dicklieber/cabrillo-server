
package org.wa9nnn.wfdserver.contest

import java.time.Instant

import org.wa9nnn.wfdserver.util.TimeConverters.instantDisplayUTCCST
import play.api.libs.json.{Format, Json}

/**
 *
 * @param h2      header shown above message
 * @param message body the message. Will not be html-escaped, so any HTML may be used.
 */
case class Message(h2: String, message: String)

/**
 * This is persisted in the apps config:wfd.submissionControl
 *
 * @param beforeMessage          shown if current time is before [[submissionBegin]]
 * @param duringMessage          shown if current time is between [[submissionBegin]] and [[submissionEnd]]
 * @param afterMessage           shown if current time is after [[submissionEnd]]
 * @param times                  start end of submission period.
 */
case class SubmissionConfig(beforeMessage: Message, duringMessage: Message, afterMessage: Message, times: Times) {

  lazy val before: CurrentSubmissionState = {
    CurrentSubmissionState(submissionsAllowed = false,
      message = beforeMessage,
      times = times.display)
  }
  lazy val during: CurrentSubmissionState = {
    CurrentSubmissionState(submissionsAllowed = true,
      message = duringMessage,
      times = times.display)
  }
  lazy val afer: CurrentSubmissionState = {
    CurrentSubmissionState(submissionsAllowed = true,
      message = afterMessage,
      times = times.display)
  }
}

case class Times(submissionBegin: Instant, submissionEnd: Instant) {
  lazy val display:String = {
    s"${instantDisplayUTCCST(submissionBegin)} through ${instantDisplayUTCCST(submissionEnd)}"
  }
}

object SubmissionConfig {
  val default: SubmissionConfig = {
    SubmissionConfig(
      beforeMessage = Message(
        h2 = "Submission not yet open!",
        message = """Logs cannot be submitted until the end of Winter Field Day."""
      ),
      duringMessage = Message(
        h2 = "Welcome to submit WFD Log files.",
        message = """Choose a <a href="http://wwrof.org/cabrillo/cabrillo-specification-v3/">cabrillo file< to submit. Defails at <a href="https://a2a53e2b-2285-4083-9cff-c99fe5ba1658.filesusr.com/ugd/1c7085_8fa2f4f66e5e40b29a79d014ec53578f.pdf"> """
      ),
      afterMessage = Message(
        h2 = "Winter Field Day Logs no longer accepted!",
        message = "The log submission period is over. Please come back again next year."
      ),
      times = Times(Instant.parse("2020-01-26T19:00:00Z"),
        Instant.parse("2020-03-01T00:00:00Z"))
    )
  }
}


/**
 * Used by view
 *
 * @param submissionsAllowed true to allow uploading log file.
 * @param message            part of UI.
 * @param times              start end of submission
 */
case class CurrentSubmissionState(submissionsAllowed: Boolean, message: Message, times: String)


object Message {
  implicit val timesFormat: Format[Times] = Json.format[Times]
  implicit val configMessageFormat: Format[Message] = Json.format[Message]
  implicit val configFormat: Format[SubmissionConfig] = Json.format[SubmissionConfig]

}