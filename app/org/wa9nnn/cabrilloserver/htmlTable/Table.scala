package org.wa9nnn.cabrilloserver.htmlTable

import scala.reflect.ClassTag

/**
 * Something that can be rendered as an html <table> using the play Whirl template /app/views/renderTable.scala.html
 *
 * @param headers       zero or more rows of column headers [[Cell]]s that makeup the <thead>
 * @param rows          in the <tbody>
 */
case class Table(headers: Seq[Seq[Any]], rows: Seq[Row], id: Option[String] = None, cssClass: String = "headeredTable") {

  val columnHeaders: Seq[Seq[Cell]] = headers.map(hw => hw.map(Cell(_)))

  def withId(id: String): Table = copy(id = Option(id))

  def withCssClass(cssClass: String): Table = copy(cssClass = cssClass)
}

object Table {
  /**
   * A single row of column headers
   * Note because of Java Type erasure this ctor can't also use [[Seq]] for the headers, so [[List]] allows
   * the scala compiler to differentiate between the main ctor and the auxiliary ctor.
   *
   * This allows a vararg for rows to enable a less cluttered API e.g.
   * {{
   * new UiTable(
   * Header("A", "B", "C"),
   * UiRow("a", "b", "c")),
   * UiRow("aa", "bb", "cc"))
   * }}
   *
   * @param columnHeaders Either a [[List[TableCell]] or any other value that [[Cell]] can accept.
   * @param rows          <tbody>
   */
  def apply(columnHeaders: List[Any], rows: Row*): Table = {
    Table(Seq(columnHeaders.map(Cell(_))), rows)
  }

  /**
   * A table with one column.
   * @param singleColHeader column header
   * @param rowValues values for row.
   * @return
   */
  def apply(singleColHeader:String, rowValues:Seq[Any]):Table = {
    val rows: Seq[Row] = rowValues.map{ v =>
      Row(Seq(Cell(v)))
    }
    Table(Header(singleColHeader), rows:_*)

  }
  /**
   * A multi line header
   *
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
  def apply(header: Header, rows: Row*): Table = {
    Table(header.rows, rows)
  }

  /**
   * The ultimate UI table shortcut
   *
   * @param caseClasses a seq of
   * @tparam T any case class .
   * @return
   */
  def apply[T: ClassTag](caseClasses: Seq[Product]): Table = {
    val header = Header[T](Option(caseClasses.length))

    val rows = caseClasses.map { product =>
      Row(product)
    }
    Table(header, rows: _*)
  }

}





