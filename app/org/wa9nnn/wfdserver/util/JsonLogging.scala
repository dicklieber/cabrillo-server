
package org.wa9nnn.wfdserver.util

import java.time.Instant

import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json._

trait JsonLogging {
  /**
   * @param reason value for the reason field.
   */
  def logJson(reason: String): LogJson = {
    LogJson(logger,  Seq(LogJson.proc("reason", reason)))
  }

  /**
   * @param name to be used instead of getclass.getname.
   * @throws IllegalStateException if invoked after access to [[Logger]] or if invoked more than once.
   */
  def setLoggerName(name: String): Unit = {
    if (_loggerName.nonEmpty) {
      throw new IllegalStateException(s"LoggerName is already set for ${getClass.getName}!")
    }
    else {
      _loggerName = Some(name)
    }
  }

  private var _loggerName: Option[String] = None

  lazy val logger: Logger = {
    if (_loggerName.isEmpty) {
      _loggerName = Some(getClass.getName)
    }
    LoggerFactory.getLogger(_loggerName.get)
  }


}


case class LogJson(logger: Logger, fields: Seq[(String, JsValue)]) {

  /**
   *
   * @param newFields to be added to the [[LogJson]]
   * @return a new [[LogJson]].
   */
  def ++(newFields: (String, Any)*): LogJson = {
    val resultFields = newFields.foldLeft(fields) { case (accum, (label, value)) =>
      accum :+ LogJson.proc(label, value)
    }
    copy(fields = resultFields)
  }


  def finish: String = {
    val map: Seq[(String, JsValue)] = fields.map(t => t)
    val jsObject: JsObject = JsObject(map)
    jsObject.toString()
  }

  def info(): Unit = logger.info(finish)

  def debug(): Unit = logger.debug(finish)

  def trace(): Unit = logger.trace(finish)

  def error(): Unit = logger.error(finish)

  def error(cause: Throwable): Unit = logger.error(finish, cause)

  def warn(): Unit = logger.warn(finish)

}

object LogJson {
  def apply(logger: Logger): LogJson = {
    new LogJson(logger, Seq.empty)
  }

  def proc(label: String, value: Any): (String, JsValue) = {
    val jsValue = value match {
      case v: String => JsString(v)
      case v: Instant => JsString(v.toString)
      case v: Int => JsNumber(v)
      case v: Long => JsNumber(v)
      case v: Double => JsNumber(v)
      case v: Float => JsNumber(v.toDouble)
      case v: JsObject => v
      case x => JsString(Option(x).fold("null")(_.toString))
    }
    (label, jsValue)
  }
}
