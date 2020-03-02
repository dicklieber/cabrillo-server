package com.wa9nnn.wfdserver.model

import com.wa9nnn.wfdserver.model.CallSign.regx
import play.api.data.Forms._
import play.api.data._
import play.api.data.format.Formatter
import play.api.libs.json._
import play.api.mvc.QueryStringBindable

import scala.language.implicitConversions
import scala.util.matching.Regex

class CallSign private(val cs: String) extends Ordered[CallSign] {

  override def equals(obj: Any): Boolean = obj match {
    case cs: CallSign => this.cs equals cs.cs
    case _ => false
  }

  /**
   *
   * @return only safe chars to use in a filename.
   */
  def fileSafe: String = cs.replaceAll("/", "")

  override def toString: String = cs

  override def compare(that: CallSign): Int = {
    this.cs compareTo that.cs
  }

  def valid(): Unit = {
    if (!regx.matches(cs)) {
      throw new IllegalArgumentException("Bad callsign syntax!")
    }
  }

}

object CallSign {
  def empty: CallSign = new CallSign("")

  implicit def apply(callSign: CallSign): Option[CallSign] = Option(callSign)

  /**
   *
   * @param c candidate
   * @throws IllegalArgumentException if bad syntax
   * @return
   *
   */
  implicit def apply(c: String): CallSign = {
    if (c.isEmpty) {
      throw new IllegalArgumentException("Can't have empty CallSign") //todo replace with regex check
    }
    new CallSign(c.trim.toUpperCase)
  }


  def unapply(callSign: CallSign): Option[String] = {
    Option(callSign.cs)
  }

  /**
   * for play json
   */
  implicit val callSignFormat: Format[CallSign] = new Format[CallSign] {
    override def reads(json: JsValue): JsResult[CallSign] = {
      val cs = json.as[String]
      try {
        JsSuccess(CallSign(cs))
      }
      catch {
        case e: IllegalArgumentException ⇒ JsError(e.getMessage)
      }
    }

    override def writes(callSign: CallSign): JsValue = {
      JsString(callSign.toString)
    }
  }


  implicit def callSignFormatter: Formatter[CallSign] = new Formatter[CallSign] {


    override def bind(key: String, data: Map[String, String]) = {
      try {
        data.get(key)
          .map{c =>
            val cs: CallSign = CallSign(c)
            cs.valid()
            cs
          }
          .toRight(Seq(FormError(key, "error.required", "Required")))
      } catch {
        case e: IllegalArgumentException =>
          val error = FormError(key,  e.getMessage)
          Left(Seq(error))
      }
    }

    override def unbind(key: String, value: CallSign): Map[String, String] = {
      Map(key -> value.toString)
    }
  }


  val callSign: Mapping[CallSign] = of[CallSign]

  // for routes
  implicit object bindableChar extends QueryStringBindable[CallSign] {

    override def unbind(key: String, value: CallSign): String = {
      s"$key=$value"
    }

    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, CallSign]] = {
      for {
        s <- params.get(key)
        cs <- s.headOption
      } yield {
        try {
          Right(CallSign(cs))
        } catch {
          case _: NoSuchElementException =>
            Left("CallSign is required!")
          case _: IllegalArgumentException =>
            Left("Bad CallSign Syntax")
          case x: Exception =>
            Left(x.getMessage)
        }
      }
    }
  }

  // for play form to bind/unbind with a form
  implicit object playFormFormatter extends play.api.data.format.Formatter[CallSign] {
    override def unbind(key: String, value: CallSign): Map[String, String] = {
      Seq(key -> value.toString).toMap
    }

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], CallSign] = {
      try {
        val callSign1 = CallSign(data(key))
        callSign1.valid()
        Right(callSign1)
      } catch {
        case _: NoSuchElementException =>
          Left(Seq(FormError(key, "CallSign is required!")))
        case _: IllegalArgumentException =>
          Left(Seq(FormError(key, "Bad CallSign Syntax", "Bad CallSign Syntax")))
        case x: Exception =>
          Left(Seq(FormError(key, x.getMessage)))
      }
    }
  }

  val regx: Regex = """[a-zA-Z0-9]{1,3}[0123456789][a-zA-Z0-9]{0,3}[a-zA-Z]""".r

}

