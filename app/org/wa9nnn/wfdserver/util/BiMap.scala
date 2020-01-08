
package org.wa9nnn.wfdserver.util

import scala.util.control.Exception._

// copied this from https://stackoverflow.com/questions/9850786/is-there-such-a-thing-as-bidirectional-maps-in-scala
// I don't understand the MethodDistinctor stuff, but it wprks
object BiMap {

  private[BiMap] trait MethodDistinctor

  implicit object MethodDistinctor extends MethodDistinctor

}

/**
 * A bi-directional map. Allows lookups of X to Y oe Y to X.
 *
 * @param map a map
 * @tparam X key
 * @tparam Y value
 */
class BiMap[X, Y](map: Map[X, Y]) {
  /**
   *
   * @param tuples X -> Y *
   */
  def this(tuples: (X, Y)*) = this(tuples.toMap)

  private val reverseMap = map map (_.swap)
  require(map.size == reverseMap.size, "no 1 to 1 relation")

  def apply(x: X): Y = map(x)

  def apply(y: Y)(implicit d: BiMap.MethodDistinctor): X = {
    try {
      reverseMap(y)
    } catch {
      case _: Throwable => domain.head
    }
  }

  /**
   *
   * @param y maybe a Y
   * @return None if y was already None or Y not it map.
   */
  def apply(y: Option[Y]): Option[X] = {
    y.flatMap { b =>
      allCatch opt apply(b)
    }
  }

  def apply(x: Option[X], default: Y): Y = {
    x.map(map(_)).getOrElse(default)
  }

  val domain: Iterable[X] = map.keys
  val codomain: Iterable[Y] = reverseMap.keys
}

