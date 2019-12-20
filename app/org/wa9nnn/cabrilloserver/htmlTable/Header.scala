
package org.wa9nnn.cabrilloserver.htmlTable

import scala.reflect.{ClassTag, _}

/**
 * Helper to build Seq[Seq[TableCell
 * use apply for useful construction.
 *
 * @param rows header rows.
 */
case class Header(rows: Seq[Seq[Cell]])

object Header {
  /**
   *
   * @param allColHeader top row header that will span all subheaders
   * @param subheaders   subheaders.
   * @return
   */
  def apply(allColHeader: String, subheaders: Any*): Header = {
    val topHeader = Cell(allColHeader).withColSpan(subheaders.length)
    Header(Seq(
      Seq(Cell(topHeader)
        .withCssClass("sorter-false")),
      subheaders.map(Cell(_))
    ))
  }

  /**
   * Infer names from a class
   *
   * @tparam T usually a case class to produce a [[Header]] that works with [[Row(Product)]]nU
   * @return
   */
  def apply[T: ClassTag](count: Option[Int] = None): Header = {
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
    Header(top, names.toIndexedSeq)
  }
}
