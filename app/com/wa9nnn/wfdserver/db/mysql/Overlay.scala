
package com.wa9nnn.wfdserver.db.mysql

import com.wa9nnn.wfdserver.util.BiMap

object Overlay extends BiMap[Int, String](
  0 -> "N/A",
  1 -> "CLASSIC",
  2 -> "ROOKIE",
  3 -> "TB-WIRES",
  4 -> "NOVICE-TECH",
  5 -> "OVER-50"
)

