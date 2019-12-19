/*
 * Copyright (c) 2015 HERE All rights reserved.
 */

package org.wa9nnn.cabrilloserver.util

import java.time.Instant

import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json._

/**
 * An slf4j [[org.slf4j.Logger]]
 * Uses the java class name as the LoggerName. Can be changed by invoking [[.setLoggerName]] before using any logger.
 * Can produce a [[LogJson]] that easily generates JSON log messages that are very friendly to  [[https://www.elastic.co LogStash and the ELK stack.]]
 */

trait JsonLogging {

  /**
   * Change logger name from default package/class.
   *
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

  /**
   * @param reason value for the reason field.
   * @return see [[LogJson]]
   */
  def logJson(reason: String): LogJson = {
    LogJson(logger)
      .field("reason", reason)
  }
}

/**
 * Convenience class to build structured, json, log messages.
 *
 * Instances of [[LogJson]] are usually created by invoking [[JsonLogging.logJson]]
 * {{{
 *     logJson("statusChange")
 * .field("ServiceArea", serviceAreaKey)
 * .field("hdType", importerMessage.messageType)
 * .field("hdTid", importerMessage.hdTid)
 * .field("chunks", importerMessage.body.size)
 * .field("bytes", byteCount)
 * .field("origFile", importerMessage.productDestination.getFileName)
 * .field("origSize", importerMessage.originalSize)
 * .info()
 * }}}
 *
 * It's often helpful to use a curry-like idiom:
 * {{{
 * val logServiceAreaJson = logJson.field("ServiceArea", serviceAreaKey)
 * ...
 * logServiceAreaJson
 * .field("hdType", importerMessage.messageType)
 * .field("hdTid", importerMessage.hdTid)
 * .field("chunks", importerMessage.body.size)
 * .field("bytes", byteCount)
 * .field("origFile", importerMessage.productDestination.getFileName)
 * .field("origSize", importerMessage.originalSize)
 * .info()
 * }}}
 *
 * @param logger that will be used when one of the info, debug etc. methods are called.
 * @param fields all the fields added with [[.field]]
 */
class LogJson(logger: Logger, val fields: Seq[(String, JsValue)]) {

  def field(label: String, value: Any): LogJson = {
    new LogJson(logger, fields :+ LogJson.proc(label, value))
  }

  /**
   * Allows adding bulk fields.
   *
   * @param newFields to be added to the [[LogJson]]
   * @return a new [[LogJson]]/.
   */
  def ++(newFields: Seq[(String, Any)]): LogJson = {
    val resultFields = newFields.foldLeft(fields) { case (accum, (label, value)) =>
      accum :+ LogJson.proc(label, value)
    }
    new LogJson(logger, resultFields)
  }

  /**
   * Add one field
   * @param newField name -> value
   * @return a new [[LogJson]].
   */
  def ++(newField:(String, Any)): LogJson = {
    val finalFields = fields :+ LogJson.proc(newField._1, newField._2)
    new LogJson(logger, finalFields)
  }

  def info(): Unit = logger.info(render)

  def debug(): Unit = logger.debug(render)

  def trace(): Unit = logger.trace(render)

  def error(): Unit = logger.error(render)

  def error(cause: Throwable): Unit = logger.error(render, cause)

  def warn(): Unit = logger.warn(render)

  def render: String = {
    val map: Seq[(String, JsValue)] = fields.map(t => t)
    val jsObject: JsObject = JsObject(map)
    jsObject.toString()
  }

}

object LogJson {

  def apply(logger: Logger): LogJson = {
    new LogJson(logger, Seq.empty)
  }

  def proc(label: String, value: Any): (String, JsValue) = {
    val jsValue = value match {
      case v: String => JsString(v)
      case v: Instant => JsString(v.toString)
//      case v: ZonedDateTime => JsString(v.toString)
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
