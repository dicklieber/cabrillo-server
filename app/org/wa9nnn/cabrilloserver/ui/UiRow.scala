package org.wa9nnn.cabrilloserver.ui


/**
  * An html <tr> element.
  *
  * @param cells      <td> elements for the row.
  * @param rowId      if Some, this is the id attribute of the <tr>
  * @param cssClass   css classes for the <tr> element.
  * @param rowToolTip tool tip.
  */
case class UiRow(cells: Seq[TableCell],
                 rowId: Option[String] = None,
                 cssClass: Seq[String] = Seq.empty,
                 rowToolTip: String = "") {
  def withToolTip(toolTip: String): UiRow = copy(rowToolTip = toolTip)

  def withCssClass(cssClass: String): UiRow = copy(cssClass = this.cssClass :+ cssClass)

  /**
    * Append a cell to this row.
    *
    * @param in to be appended. If this is a [[TableCell]] it is appended.
    *           If any other type, a new [[TableCell]] will be created with the value.
    * @return a new UiRow with additional cell.
    */
  def :+(in: Any): UiRow = {
    copy(cells = cells :+ TableCell(in))
  }

  /**
    *
    * @return all the cssClases seperated by a space.
    */
  def renderedCssClass: String = {
    cssClass.mkString(" ")
  }

}

object UiRow {
  /**
    *
    * @param rowHeader text for leftmost column in row.
    * @param values    values for subsiquent cells in the row.
    * @return
    */
  def apply(rowHeader: String, values: Any*): UiRow = {
    val headerCell = TableCell(rowHeader)
    val subsiquentCells = values.map(TableCell(_))
    UiRow(headerCell +: subsiquentCells)
  }

  /**
    *
    * @param headerCell  leftmost column in row.
    * @param values      values for subsiquent cells in the row.
    * @return
    */
  def apply(headerCell: TableCell, values: Any*): UiRow = {
    val subsiquentCells = values.map(TableCell(_))
    UiRow(headerCell +: subsiquentCells)
  }

  /**
    * Works for any case class assuming types are understood by [[TableCell]] for complex i.e. pareters that are other case classes this will
    * make [[TableCell]]s that just invoke toString, which may not be what you want.
    * Usually you'll want to implement your own method on the case class thqt returns a UiRow so you have full control over order.
    * If you use this use [[Header[T]]] to build a header to make a UiTable
    *
    * @param anyCaseClass case classes automatically impement [[Product]]
    * @return
    */
  def apply(anyCaseClass: Product): UiRow = {
    val r: Seq[TableCell] = anyCaseClass.productIterator.map(TableCell(_)).toSeq
    new UiRow(r)
  }
}