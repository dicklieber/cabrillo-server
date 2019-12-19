package org.wa9nnn.cabrilloserver.htmlTable

/**
 * Creates a [[Table]] with cells ordered in columns.
 *
 *
 */
object MultiColumn {
  /**
   * Build a [[Seq[Seq[Any]] with items organized into columns.
   *
   * @param items      each will become [[Cell]] in column,
   * @param max        number of columns, number of rows will be determined automatically.
   * @return
   */
  def organize(items: Seq[Any], max: Int, emptyCell: Any = "-"): Seq[Seq[Any]] = {
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

      val grid: Seq[Seq[Any]] = items.grouped(columnHeight).toSeq
      val nRows = grid.head.length
      val nCols = grid.length
      for {
        row <- 0 until nRows
      } yield {
        val r = for {
          col <- 0 until nCols
          column = grid(col)
        }
          yield {
            try {
              column(row)
            } catch {
              case e: Exception =>
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
   * @param max           number of columns, number of rows will be determined
   * @param header        non empty to create header that spans the table
   * @param emptyCell     what to put is cells beyond thise in [[items]].
   * @param noDataMessage show this if there are no items.
   * @return
   */
  def apply(items: Seq[Any], max: Int, header: String = "", emptyCell: String = "", noDataMessage: String = "No data available yet!"): Table = {
    if (items.isEmpty) {
      Table(List.empty, Row(noDataMessage))
    } else {

      val organizedByColumn: Seq[Row] = organize(items, max, emptyCell).map { rowOfAny => Row(rowOfAny.map(Cell(_))) }
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
