
package com.wa9nnn.wfdserver.paper

import java.nio.file.{Files, Path, StandardOpenOption}

import com.wa9nnn.wfdserver.auth.WfdSubject
import com.wa9nnn.wfdserver.model.PaperLogQso
import com.wa9nnn.wfdserver.util.Page

import scala.io.Source
import scala.util.Using
/**
 * Handles QSO persistence with caching.
 *
 * @param ourDir     where our file will live.
 * @param wfdSubject who.
 */
class QsoCache(val ourDir: Path)(implicit wfdSubject: WfdSubject) {
  val qsosFile: Path = ourDir.resolve("qsos.txt")
  private var cache: IndexedCache = readFile

  def size: Int = cache.size

  def add(qso: PaperLogQso): Unit = {
    cache += qso
    Using(Files.newOutputStream(qsosFile,
      StandardOpenOption.WRITE,
      StandardOpenOption.CREATE,
      StandardOpenOption.APPEND)) { outputStream =>
      outputStream.write(qso.toCsvLine.getBytes())
      outputStream.write('\n')
    }
  }

  /**
   * .
   * @param index to be removed.
   * @return new collection of QSOs to be persisted
   */
  def remove(index:Int): Unit = {
    cache.remove(index)

    Using(Files.newOutputStream(qsosFile,
      StandardOpenOption.WRITE,
      StandardOpenOption.CREATE)) { outputStream =>
      cache.all.foreach{qso =>
        outputStream.write(qso.toCsvLine.getBytes())
        outputStream.write('\n')
      }
    }

    }
  /**
   *
   * @param page None gets all, ter
   * @return
   */
  def page(page: Option[Page] = None): Iterable[PaperLogQso] = {
    cache.page(page)
  }
  def lastPage: Iterable[PaperLogQso] = {
    cache.lastPage
  }

  def all:Iterable[PaperLogQso] = cache.all
  def get(index:Int):Option[PaperLogQso] = cache.get(index)

  private def readFile: IndexedCache = {
    Using(Source.fromFile(qsosFile.toFile)) { bs =>
      new IndexedCache(bs.getLines)
    }.getOrElse(new IndexedCache())
  }
}

object QsoCache {
  val defaultPageSize: Int = 25
}

