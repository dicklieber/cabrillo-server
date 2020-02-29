
package com.wa9nnn.wfdserver.play

import java.net.URL
import java.util

import play.api.data.Forms.of
import play.api.data.{FormError, Mapping}
import play.api.data.format.{Formats, Formatter}
import play.api.libs.json.{Format, JsError, JsResult, JsString, JsSuccess, JsValue}

object EnumPlayUtils {
  //------------------------------------------------------------------------
  // public
  //------------------------------------------------------------------------
  /**
   * Constructs a simple mapping for a text field (mapped as `scala.Enumeration`)
   *
   * For example:
   * {{{
   * Form("gender" -> enum(Gender))
   * }}}
   *
   * @param enum the enumeration
   */
  def enum[E <: Enumeration](enum: E): Mapping[E#Value] = of(enumFormat(enum))

  /**
   * Default formatter for `scala.Enumeration`
   *
   */
  def enumFormat[E <: Enumeration](enum: E): Formatter[E#Value] = new Formatter[E#Value] {
    def bind(key: String, data: Map[String, String]) = {
      Formats.stringFormat.bind(key, data).right.flatMap { s =>
        scala.util.control.Exception.allCatch[E#Value]
          .either(enum.withName(s))
          .left.map(e => Seq(FormError(key, "Must be one of: " + enum.values.mkString(","), Nil)))
      }
    }

    def unbind(key: String, value: E#Value) = Map(key -> value.toString)
  }



}
