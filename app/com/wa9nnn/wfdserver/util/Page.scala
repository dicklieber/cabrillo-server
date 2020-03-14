
package com.wa9nnn.wfdserver.util


case class Page(pageNo: Int, pageSize: Int = 25){
  def from: Int = pageNo * pageSize
  def until: Int = from + pageSize
  def start :Int = pageNo * pageSize
  def end:Int = start + pageSize
}

object Page {
  val last:Page = new Page(pageNo = -1)
  val all:Page = new Page(pageNo = -2)
}

