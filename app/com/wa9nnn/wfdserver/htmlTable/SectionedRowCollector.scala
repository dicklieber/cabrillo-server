
package com.wa9nnn.wfdserver.htmlTable

class SectionedRowCollector(val rows: Seq[Row] = Seq.empty) {

  def +=(seperator: String, rows: Seq[Row]): SectionedRowCollector = {

    val sectionHeader = Row(Seq(Cell(seperator)
      .withCssClass("sectionHeader")
      .withColSpan(rows.length)
    ))


    new SectionedRowCollector(this.rows :+ sectionHeader :++ rows)
  }

  def +=(seperator: Any, columns: Seq[Any], rows: Seq[Row]): SectionedRowCollector = {


    val colHeaders: Row = Row(columns.map {
      Cell(_)
    }).withCssClass("sectionHeader")

    val cols = colHeaders.cells.foldLeft(0){case (accum, cell) => accum + cell.colSpan}
    val sectionHeader = Row(Seq(Cell(seperator)
      .withCssClass("sectionHeader")
      .withColSpan(cols)
    ))

    new SectionedRowCollector(this.rows :+ sectionHeader :+ colHeaders :++ rows)
  }
  def += (sectionRows:SectionedRows):SectionedRowCollector = {

    val colHeaders: Row = Row(sectionRows.columns.map {
      Cell(_)
    }).withCssClass("sectionHeader")

    val cols = colHeaders.cells.foldLeft(0){case (accum, cell) => accum + cell.colSpan}
    val sectionHeader = Row(Seq(Cell(sectionRows.sectionName)
      .withCssClass("sectionHeader")
      .withColSpan(cols)
    ))

    new SectionedRowCollector(this.rows :+ sectionHeader :+ colHeaders :++ rows)

  }
}

case class SectionedRows(sectionName:String, columns: Any*)(rows:Seq[Row])