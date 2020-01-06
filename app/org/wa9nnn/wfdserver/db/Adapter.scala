
package org.wa9nnn.wfdserver.db

import java.time.Instant

import org.wa9nnn.cabrillo.model.CabrilloData
import org.wa9nnn.cabrillo.model.CabrilloTypes.Tag

/**
 * Things useful for MongoDB or MySQL databases
 */
trait Adapter {
  implicit val cabrilloData:CabrilloData

  /**
   *
   * @param tag name
   * @return body of 1st instance of the [[Tag]] or None if tag not present
   */
   implicit def str(tag: Tag): Option[String] = {
    cabrilloData.apply(tag).headOption.map(_.body)
  }
  implicit def s(tag: Tag): String = {
    cabrilloData.apply(tag).headOption.map(_.body).getOrElse("")
  }

   implicit def bol(t: (Tag, String)): Option[Boolean] = {
    cabrilloData.apply(t._1).headOption.map(_.body.toUpperCase() == t._2)
  }

   implicit def intO(tag: Tag): Option[Int] = {
    cabrilloData.apply(tag).headOption.map(_.body.toInt)
  }
   def int(tag: Tag): Int= {
    cabrilloData.apply(tag).headOption.map(_.body.toInt).get
  }

  implicit def asDate(stamp: Instant): java.sql.Date = {
    new java.sql.Date(stamp.toEpochMilli)
  }

  implicit def asTime(stamp: Instant): java.sql.Time = {
    new java.sql.Time(stamp.toEpochMilli)
  }

  def callsign: String = cabrilloData.apply("CALLSIGN").head.body


}
