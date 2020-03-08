
package com.wa9nnn.wfdserver.paper

import com.wa9nnn.wfdserver.model.PaperLogQso
import com.wa9nnn.wfdserver.paper.QsoCache.defaultPageSize
import com.wa9nnn.wfdserver.util.{JsonLogging, Page}

import scala.collection.Iterator
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Exception.allCatch

class IndexedCache(csvLines: Iterator[String] = Iterator.empty) extends JsonLogging{
  def size: Int = qsos.size

  private val qsos = new ArrayBuffer[PaperLogQso]
  private var index = 0

  csvLines.foreach { line =>
    try {
      qsos += PaperLogQso.fromCsv(line, index)
      index += 1
    } catch {
      case e:Exception =>
        logger.error(s"""Ignoring line: "$line"""", e)
    }
  }

  def +=(qso: PaperLogQso): Unit = {
    qsos += qso.withIndex(index)
    index += 1
  }

  def get(index: Int): Option[PaperLogQso] = {
    allCatch opt qsos(index)
  }
  def all:Iterable[PaperLogQso] = qsos

  def page(page: Option[Page] = None): Iterable[PaperLogQso] = {
    page match {
      case Some(page) =>
        qsos.slice(page.from, page.until)
      case None =>
        qsos
    }
  }

    def remove(index:Int):Unit = qsos.remove(index)

  def lastPage: Iterable[PaperLogQso] = {
    if (qsos.isEmpty) {
      IndexedSeq.empty[PaperLogQso]
    } else {
      qsos.takeRight(defaultPageSize)
    }
  }
}
