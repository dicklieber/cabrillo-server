package com.wa9nnn.wfdserver.htmlTable


/**
 * An html table row, <tr>.
 *
 * @param cells      cells for the row.
 * @param rowId      if Some, this is the id attribute of the <tr>
 * @param cssClass   css classes for the row.
 * @param rowToolTip tool tip. Non-empty string will become a tooltip.
 */
case class Row(cells: Seq[Cell],
               rowId: Option[String] = None,
               cssClass: Seq[String] = Seq.empty,
               rowToolTip: String = "")
  extends RowSource {
  def withToolTip(toolTip: String): Row = copy(rowToolTip = toolTip)

  /**
   * Addpend this cssClass to any exitings classes.
   * @param cssClass to be appended.
   * @return new Row with additional cssClass.
   */
  def withCssClass(cssClass: String): Row = copy(cssClass = this.cssClass :+ cssClass)

  /**
   * Append a cell to this row.
   *
   * @param in to be appended. If this is a [[Cell]] it is appended.
   *           If any other type, a new [[Cell]] will be created with the value.
   * @return a new UiRow with additional cell.
   */
  def :+(in: Any): Row = {
    copy(cells = cells :+ Cell(in))
  }

  /**
   *
   * @return all the cssClasses separated by a space.
   */
  def renderedCssClass: String = {
    cssClass.mkString(" ")
  }
  def withId(id:String):Row = copy(rowId = Some(id))

  override def toRow: Row = this
}

object Row {
  /**
   *
   * @param rowHeader text for leftmost column in row.
   * @param values    values for subsequent cells in the row.
   * @return a Row.
   */
  def apply(rowHeader: String, values: Any*): Row = {
    val headerCell = Cell(rowHeader)
    val subsiquentCells = values.map(Cell(_))
    Row(headerCell +: subsiquentCells)
  }

  /**
   *
   * @param headerCell  leftmost column in row.
   * @param values      values for subsiquent cells in the row.
   * @return
   */
  def apply(headerCell: Cell, values: Any*): Row = {
    val subsiquentCells = values.map(Cell(_))
    Row(headerCell +: subsiquentCells)
  }

  /**
   * Works for any case class assuming types are understood by [[Cell]] for complex i.e. parameters that are other case classes this will
   * make [[Cell]]s that just invoke toString, which may not be what you want.
   * Usually you'll want to implement your own method on the case class thqt returns a UiRow so you have full control over order.
   * If you use this use [[Header[T]]] to build a header to make a UiTable
   *
   * @param anyCaseClass case classes automatically impement [[Product]]
   * @return
   */
  def apply(anyCaseClass: Product): Row = {
    val r: Seq[Cell] = anyCaseClass.productIterator.map(Cell(_)).toSeq
    new Row(r)
  }
}