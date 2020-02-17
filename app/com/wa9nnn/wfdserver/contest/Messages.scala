
package com.wa9nnn.wfdserver.contest

import java.time.Instant

import com.wa9nnn.wfdserver.util.MarkDown
import com.wa9nnn.wfdserver.util.TimeConverters.instantDisplayUTCCST
import play.api.libs.json.{Format, Json}
/**
 *
 * @param h2      header shown above message
 * @param message body the message. Will not be html-escaped, so any HTML may be used.
 */
case class Message(h2: String, message: String) {
 val md:String = {
   MarkDown(message)
 }
}

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

  def messagesMap: Map[String, String] = Seq(
    "before" -> beforeMessage.message,
    "during" -> duringMessage.message,
    "after" -> afterMessage.message,
  ).toMap
}

case class Times(submissionBegin: Instant, submissionEnd: Instant) {
  lazy val display: String = {
    s"${instantDisplayUTCCST(submissionBegin)} through ${instantDisplayUTCCST(submissionEnd)}"
  }
}

object SubmissionConfig {
  val default: SubmissionConfig = {
    SubmissionConfig(
      beforeMessage = Message(h2 = "Submission not yet open!", message = """Logs cannot be submitted until the **end** of Winter Field Day."""),
      duringMessage = Message(h2 = "Welcome to submit WFD Log files.", message =
                """If you make mistake, you can simply resubmit your cabrillo file here.
                  |The latest submitted file will be used, overwriting previous submissions.
                  |""".stripMargin),
      afterMessage = Message(h2 = "Winter Field Day Logs no longer accepted!", message = "The log submission period is over. Please come back again next year."),
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


