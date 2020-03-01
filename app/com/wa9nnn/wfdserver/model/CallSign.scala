package com.wa9nnn.wfdserver.model

import javax.inject.Singleton
import org.bson.codecs.{Codec, DecoderContext, EncoderContext}
import org.bson.{BsonReader, BsonWriter}
import play.api.data.Forms._
import play.api.data._
import play.api.data.format.Formatter
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.libs.json.{Format, Json}
import play.api.mvc.QueryStringBindable

class CallSign private(val cs: String) extends Ordered[CallSign] {
  /**
   *
   * @return only safe chars to use in a filename.
   */
  def fileSafe: String = cs.replaceAll("/", "")

  override def toString: String = cs

  override def compare(that: CallSign): Int = {
    this.cs compareTo that.cs
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
    new CallSign(c.toUpperCase)
  }

  def isValid(callSign: String): Boolean = {
    true //todo use regex
  }

  def unapply(callSign: CallSign): Option[String] = {
    Option(callSign.cs)
  }

  //  /**
  //   * Lets [[CallSign]] work directly in a Play Form.
  //   */
  //  val callSignForm: Form[CallSign] = Form(
  //    mapping(
  //      "cs" -> text
  //    )(CallSign.apply)(CallSign.unapply)
  //
  //  )
  val callSignFormat: Format[CallSign] = Json.format[CallSign]


  implicit def callSignFormatter: Formatter[CallSign] = new Formatter[CallSign] {


    override def bind(key: String, data: Map[String, String]) = {
      data.get(key)
        .map(CallSign(_))
        .toRight(Seq(FormError(key, "error.required", Nil)))
    }

    override def unbind(key: String, value: CallSign):Map[String, String] = {
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

  @Singleton
  class CallSignValidator {
    val categoryConstraint: Constraint[String] = Constraint("Bad CallSign") { categoryCandidate =>

      if (CallSign.isValid(categoryCandidate))
        Valid
      else
        Invalid("callsign todo")
    }
  }

  // for play form to bind/unbind with a form
  implicit object playFormFormatter extends play.api.data.format.Formatter[CallSign] {
    override def unbind(key: String, value: CallSign): Map[String, String] = {
      Seq(key -> value.toString).toMap
    }

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], CallSign] = {
      try {
        Right(CallSign(data(key)))
      } catch {
        case _: NoSuchElementException =>
          Left(Seq(FormError(key, "CallSign is required!")))
        case _: IllegalArgumentException =>
          Left(Seq(FormError(key, "Bad CallSign Syntax")))
        case x: Exception =>
          Left(Seq(FormError(key, x.getMessage)))
      }
    }
  }


}

