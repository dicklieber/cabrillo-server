
package com.wa9nnn.wfdserver.htmlTable


import scala.collection.concurrent.TrieMap

object TextRenderer {

  class Max {
    var v = 0

    def apply(s: String): Unit = apply(s.length)

    def apply(n: Int): Unit = {
      v = Math.max(v, n)
    }
  }


  def apply(rows: Seq[Row]): String = {
    val maxColWidths = new TrieMap[Int, Max]
    rows.map { row: Row =>
      row.cells.zipWithIndex.map { case (cell, i) =>
        maxColWidths.getOrElseUpdate(i, new Max())
          .apply(cell.value)
      }
    }

    val s = rows.map { row: Row =>
      row.cells.zipWithIndex.map { case (cell, i) =>
        val maxCell = maxColWidths(i).v
        val value: String = cell.value
        value.padTo(maxCell, ' ')
      }.mkString("|")
    }

    s.map(s => s"|$s|")
  }.mkString("\n")
}
