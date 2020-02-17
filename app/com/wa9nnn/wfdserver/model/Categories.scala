
package com.wa9nnn.wfdserver.model

import com.wa9nnn.wfdserver.htmlTable.{Row, RowsSource}

/**
 * From CATEGORY-XXX fields in Cabrillo.
 */
case class Categories(
                       operator: Option[String] = None,
                       station: Option[String] = None,
                       transmitter: Option[String] = None,
                       power: Option[String] = None,
                       assisted: Option[String] = None,
                       overlay:Option[String]= None,
                       time:Option[String]= None,
                       band: Option[String] = None,
                       mode: Option[String] = None
                     ) extends RowsSource {
  override def toRows(includeNone: Boolean, prefix: String): Seq[Row] = {

    super.toRows(includeNone = includeNone,
      prefix = "category-")
  }

}
