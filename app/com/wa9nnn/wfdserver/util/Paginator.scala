
package com.wa9nnn.wfdserver.util

  case class Paginator[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy private val pageSize: Int = 20 // you can adjust to your preference
  lazy val prevPage: Option[Int] = Option(page - 1).filter(_ >= 0)
  lazy val nextPage: Option[Int] = Option(page + 1).filter(_ => (offset + items.size) < total)
  lazy val numberOfPages: Float = if (total % pageSize > 0) { total / pageSize + 1 }
  else { total / pageSize }
}
