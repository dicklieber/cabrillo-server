
package com.wa9nnn.wfdserver.paper

import com.wa9nnn.wfdserver.model.PaperLogQso
import com.wa9nnn.wfdserver.paper.QsoCache.defaultPageSize
import com.wa9nnn.wfdserver.util.{JsonLogging, Page}

import scala.collection.Iterator
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Exception.allCatch

class IndexedCache(csvLines: Iterator[String] = Iterator.empty) extends JsonLogging {
  private val qsos = new ArrayBuffer[PaperLogQso]

  def size: Int = qsos.size
  private var index = 0

  csvLines.foreach { line =>
    try {
      qsos += PaperLogQso.fromCsv(line, index)
      index += 1
    } catch {
      case e: Exception =>
        logger.error(s"""Ignoring line: "$line"  ${e.getMessage}""")
    }
  }

  def +=(qso: PaperLogQso): Unit = {
    qsos += qso.withIndex(index)
    index += 1
  }

  def get(index: Int): Option[PaperLogQso] = {
    allCatch opt qsos(index)
  }

  def all: Seq[PaperLogQso] = qsos.toSeq

  def page(page: Page): Seq[PaperLogQso] = {
    (page match {
      case Page.last =>
        qsos.takeRight(defaultPageSize)
      case Page.all =>
        qsos
      case _:Page =>
        qsos.slice(page.from, page.until)
    }).toSeq
  }

  def remove(index: Int): Unit = qsos.remove(index)

  def update(qso: PaperLogQso): Unit = {
    if (index == -1) {
      throw new IllegalArgumentException("qso must have index, not -1!")
    }
    qsos.update(qso.index, qso)

  }

}
