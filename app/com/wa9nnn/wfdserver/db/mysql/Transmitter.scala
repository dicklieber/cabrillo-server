
package com.wa9nnn.wfdserver.db.mysql

import com.wa9nnn.wfdserver.util.BiMap

object Transmitter extends BiMap[Int, String](
  0 -> "N/A",
  1 -> "ONE",
  2 -> "TWO",
  3 -> "LIMITED",
  4 -> "UNLIMITED",
  5 -> "SWL"
)

