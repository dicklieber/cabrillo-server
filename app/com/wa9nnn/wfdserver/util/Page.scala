
package com.wa9nnn.wfdserver.util


case class Page(pageNo: Int, pageSize: Int = 25){
  def from: Int = pageNo * pageSize
  def until: Int = from + pageSize
}

