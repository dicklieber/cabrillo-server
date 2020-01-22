
package org.wa9nnn.wfdserver.util

import java.util.concurrent.atomic.AtomicInteger

import org.wa9nnn.wfdserver.htmlTable.{Cell, Row, RowsSource}

import scala.collection.concurrent.TrieMap

class Counted[T] {
  private val map: TrieMap[T, AtomicInteger] = TrieMap.empty

  def apply(thing: T): Unit = {
    map.getOrElseUpdate(thing, new AtomicInteger()).incrementAndGet()
  }

  def rows:Seq[Row] = {
    map.iterator.map{case (thing, count) =>
    Row(thing.toString, count.get())
    }.toSeq
  }


}
