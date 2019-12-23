package org.wa9nnn.wfdserver.htmlTable

/**
 * Creates a [[Table]] with cells ordered in columns.
 *
 *
 */
object MultiColumn {
  /**
   * Build a [[Seq[Seq[Cell]] with items organized into columns.
   *
   * @param items      each will become [[Cell]] in column,
   * @param max        number of columns, number of rows will be determined automatically.
   * @return
   */
  def organize(items: Seq[Cell], max: Int, emptyCell: Cell): Seq[Seq[Cell]] = {
    if (items.isEmpty) {
      Seq.empty
    } else {
      val nItems = items.length
      var columnHeight = nItems / max
      if (columnHeight == 0) {
        columnHeight = nItems
      } else {
        val mod: Int = nItems % max
        if (mod > 0) {
          columnHeight += 1
        }
      }

      val grid: Seq[Seq[Cell]] = items.grouped(columnHeight).toSeq
      val nRows = grid.head.length
      val nCols = grid.length
      for {
        iRow <- 0 until nRows
      } yield {
        val r: Seq[Cell] = for {
          iCol <- 0 until nCols
          column = grid(iCol)
        }
          yield {
            try {
              column(iRow)
            } catch {
              case _: Exception =>
                emptyCell
            }
          }
        r
      }
    }
  }


  /**
   * Build a [[Table]] with items organized into columns.
   *
   * @param items         each will become [[Cell]] in column,
   * @param max           number of columns, number of rows will be determined automatically.
   * @param header        non empty to create header that spans the table
   * @param emptyCell     what to put is cells beyond those in items.
   * @param noDataMessage show this if there are no items.
   * @return
   */
  def apply(items: Seq[Cell], max: Int, header: String = "", emptyCell: Cell = Cell(""), noDataMessage: String = "No data available yet!"): Table = {
    if (items.isEmpty) {
      Table(List.empty, Row(noDataMessage))
    } else {

      val organizedByColumn: Seq[Row] = organize(items, max, emptyCell).map { rowOfCells => Row(rowOfCells) }
      val headers = if (header.nonEmpty) {
        val nCols = organizedByColumn.head.cells.length
        Seq(Seq(Cell(header).withColSpan(nCols)))
      } else {
        Seq.empty
      }
      Table(headers, organizedByColumn)
    }
  }
}

trait CellProvider {
  def toCell: Cell
}
