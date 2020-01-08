
package org.wa9nnn.wfdserver.db.mongodb


import java.util.concurrent.TimeUnit

import org.mongodb.scala._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Add some simplicity to the [[Observable]] stuff returned by teh Scala MongoDB driver.
 */
object Helpers {

  implicit class DocumentObservable[C](val observable: Observable[Document]) extends ImplicitObservable[Document] {
    override val converter: Document => String = doc => doc.toJson
  }

  implicit class GenericObservable[C](val observable: Observable[C]) extends ImplicitObservable[C] {
    override val converter: C => String = doc => doc.toString
  }

  trait ImplicitObservable[C] {
    val observable: Observable[C]
    val converter: C => String

    /**
     *
     * @param f method that take the C from the Db and maps to an R to be returned.
     * @tparam R type of result as in Seq[R].
     * @return db results as mapped.
     */
    def mapResults[R](f: C => R): Seq[R] = Await.result(observable.map(f(_)).toFuture(), Duration(10, TimeUnit.SECONDS))

    def results(): Seq[C] = Await.result(observable.toFuture(), Duration(10, TimeUnit.SECONDS))
    def headResult(): C = Await.result(observable.head(), Duration(10, TimeUnit.SECONDS))
    def printResults(initial: String = ""): Unit = {
      if (initial.length > 0) print(initial)
      results().foreach(res => println(converter(res)))
    }
    def printHeadResult(initial: String = ""): Unit = println(s"$initial${converter(headResult())}")
  }

}