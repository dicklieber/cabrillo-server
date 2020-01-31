
package org.wa9nnn.wfdserver.htmlTable

import org.wa9nnn.wfdserver.util.CountedThings

/**
 * Adapt a case class to a [[ Seq[Row] ]]
 * Nested case classes that extend [[RowsSource]] will also have their members rendered as Rows.
 * case classes extend [[Product]] which allows introspecting the contents; which is the 'magic' behind this trait.
 */
trait RowsSource extends Product {

  def toRows(includeNone: Boolean = true, prefix: String = ""): Seq[Row] = {
    val rows = (0 until productArity).iterator.toSeq.flatMap { i =>
      val name = prefix + productElementName(i)
      productElement(i) match {
        case counted:CountedThings[Any] =>
          counted.rows
        case None =>
          if (includeNone)
            Seq(Row(name, ""))
          else
            Seq.empty
        case Some(a) =>
          Seq(Row(name, a))
        case rs: RowsSource =>
          rs.toRows()
        case seq: Seq[Any] =>
          seq.map(Row(name, _))
        case nonOption =>
          Seq(Row(name, nonOption))
      }
    }
    rows
  }
}




trait RowSource {
  def toRow: Row
}

