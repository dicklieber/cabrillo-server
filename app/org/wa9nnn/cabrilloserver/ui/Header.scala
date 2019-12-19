
package org.wa9nnn.cabrilloserver.ui

import scala.collection.JavaConverters._
import scala.reflect.ClassTag
import scala.reflect._

/**
  * Helper to build Seq[Seq[TableCell
  * use apply for useful construction.
  * @param rows header rows.
  */
case class Header(rows:Seq[Seq[TableCell]])
  object Header{
  /**
    *
    * @param allColHeader top row header that will span all subheaders
    * @param subheaders subheaders.
    * @return
    */
  def apply(allColHeader: String, subheaders: Any*): Header = {
    val topHeader = TableCell(allColHeader).withColSpan(subheaders.length)
    Header(Seq(
      Seq(TableCell(topHeader)
      .withCssClass("sorter-false")),
      subheaders.map(TableCell(_))
    ))
  }

    /**
      * Infer names from a class
      * @tparam T usually a case class to produce a [[Header]] that works with [[UiRow(Product)]]nU
      * @return
      */
    def apply[T:ClassTag](count:Option[Int] = None):Header = {
      val cT = classTag[T]
      val runtimeClass = cT.runtimeClass
      val top = {
        val forClass = runtimeClass.getName.split("""\.""").last
        count match {
          case Some(x) =>
            s"$forClass ($x)"
          case None =>
            forClass
        }
      }
      val names = runtimeClass.getDeclaredFields.map(_.getName)
      Header(top, names:_*)
    }
}
