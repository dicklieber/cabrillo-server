package org.wa9nnn.cabrilloserver.ui

import scala.reflect.ClassTag
import scala.Product

/**
  * Something that will be rendered as an html <table> using the whirl template /app/views/renderTable.scala.html
  * Earlier versions of this took a Seq[Seq[TableCell]] for headers.
  *
  * @param headers       zero or more rows of column headers [[TableCell]]s that makeup the <thead>
  * @param rows          <tbody>
  */
case class UiTable(headers: Seq[Seq[Any]], rows: Seq[UiRow], id: Option[String] = None, cssClass: String = "headeredTable") {

  val columnHeaders: Seq[Seq[TableCell]] = headers.map(hw => hw.map(TableCell(_)))

  def withId(id: String): UiTable = copy(id = Option(id))

  def withCssClass(cssClass: String): UiTable = copy(cssClass = cssClass)
}

object UiTable {
  /**
    * A single row of column hneaders
    * Note because of Java Type erasure this ctor can't also use [[Seq]] for the headers, so [[List]] allows
    * the scala compiler to differentiate between the main ctor and the auxiliary ctor.
    *
    * This ctor also allows a vararg for rows to enable a less cluttered API e.g.
    * {{
    * new UiTable(
    * Header("A", "B", "C"),
    * UiRow("a", "b", "c")),
    * UiRow("aa", "bb", "cc"))
    * }}
    *
    * @param columnHeaders Either a [[List[TableCell]] or any other value that [[TableCell]] can accept.
    * @param rows          <tbody>
    */
  def apply(columnHeaders: List[Any], rows: UiRow*): UiTable = {
    UiTable(Seq(columnHeaders.map(TableCell(_))), rows)
  }

  /**
    * A multi row header
    *
    * This ctor also allows a vararg for rows to enable a less cluttered API e.g.
    * {{
    * new UiTable(
    * Header("fullspan", "colA", "colB", "colC"),
    * UiRow("a", "b", "c")),
    * UiRow("aa", "bb", "cc"))
    * }}
    *
    * @param header a [[Header]]
    * @param rows   <tbody>
    */
  def apply(header: Header, rows: UiRow*): UiTable = {
    UiTable(header.rows, rows)
  }

  /**
    * The ultimate UI table shortcut
    * @param caseClasses a seq of
    * @tparam T any case class e.g. something that extends [[Product]].
    * @return
    */
  def apply[T:ClassTag](caseClasses: Seq[Product]): UiTable = {
    val header = Header[T](Option(caseClasses.length))

    val rows = caseClasses.map { p =>
      UiRow(p)
    }
    UiTable(header, rows:_*)
  }

}





