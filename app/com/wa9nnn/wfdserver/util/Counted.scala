
package com.wa9nnn.wfdserver.util

import java.util.concurrent.atomic.AtomicInteger

import com.wa9nnn.wfdserver.htmlTable.Row

import scala.collection.concurrent.TrieMap

class Counted[T] {
  private val map: TrieMap[T, AtomicInteger] = TrieMap.empty

  def apply(thing: T): Unit = {
    map.getOrElseUpdate(thing, new AtomicInteger())
      .incrementAndGet()
  }


  /**
   * result and apply(fromResult) allows transferring an immutable object from one [[Counted]] to another.
   *
   * @return
   */
  def result: CountedThings[T] = CountedThings(map.map { case (t, atomicInteger) => t -> atomicInteger.get }.toMap)

  def apply(fromResult: CountedThings[T]): Unit = {
    fromResult.map.map { case (t, count) =>
      map.getOrElseUpdate(t, new AtomicInteger())
        .addAndGet(count)
    }
  }
}

case class CountedThings[T](map: Map[T, Int]) {
  def size:Int = map.values.sum
  def rows: Seq[Row] = {
    map.iterator.map {
      case (thing, count) =>
        Row(thing.toString, count)
    }.toSeq
  }

}
