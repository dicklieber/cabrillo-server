
package com.wa9nnn.wfdserver

object NumberOrBlank {
  def apply(number: Int, blankValue: String = "&nbsp;"): String = {
    if (number == 0)
      blankValue
    else
      f"$number%,d"
  }
}
