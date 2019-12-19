package org.wa9nnn.cabrilloserver.ui

/**
  * Creates a [[UiTable]] with cells ordered in columns.
  * {{{
  * For example:
1 11 21 31 41 51 61 71 81 91
2 12 22 32 42 52 62 72 82 92
3 13 23 33 43 53 63 73 83 93
4 14 24 34 44 54 64 74 84 94
5 15 25 35 45 55 65 75 85 95
6 16 26 36 46 56 66 76 86 96
7 17 27 37 47 57 67 77 87 97
8 18 28 38 48 58 68 78 88 98
9 19 29 39 49 59 69 79 89 99
1 11 21 31 41 51 61 71 81 91
2 12 22 32 42 52 62 72 82 92
3 13 23 33 43 53 63 73 83 93
4 14 24 34 44 54 64 74 84 94
5 15 25 35 45 55 65 75 85 95
6 16 26 36 46 56 66 76 86 96
7 17 27 37 47 57 67 77 87 97
8 18 28 38 48 58 68 78 88 98
9 19 29 39 49 59 69 79 89 99
  * }}}
  *
  *
  */
object MultiColumn {
  /**
    * Build a [[Seq[Seq[Any]] with items organized into columns.
    *
    * @param items      each will become [[TableCell]] in column,
    * @param max        number of columns, number of rows will be determined
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
    * Build a [[UiTable]] with items organized into columns.
    *
    * @param items      each will become [[TableCell]] in column,
    * @param max        number of columns, number of rows will be determined
    * @param header     non empty to create header that spans the table
    * @return
    */
  def apply(items: Seq[Any], max: Int, header: String = "", emptyCell: String = "", noDataMessage:String = "No data available yet!"): UiTable = {
    if (items.isEmpty) {
      UiTable(List.empty, UiRow(noDataMessage))
    } else {

      val organizedByColumn: Seq[UiRow] = organize(items, max, emptyCell).map { rowOfAny => UiRow(rowOfAny.map(TableCell(_))) }
      val headers = if (header.nonEmpty) {
        val nCols = organizedByColumn.head.cells.length
        Seq(Seq(TableCell(header).withColSpan(nCols)))
      } else {
        Seq.empty
      }
      UiTable(headers, organizedByColumn)
    }
  }
}
