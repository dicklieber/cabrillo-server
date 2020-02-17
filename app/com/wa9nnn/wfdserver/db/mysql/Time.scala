
package com.wa9nnn.wfdserver.db.mysql

import com.wa9nnn.wfdserver.util.BiMap

object Time extends BiMap[Int, String](
  0 -> "N/A",
  1 -> "6-HOURS",
  2 -> "12-HOURS",
  3 -> "24-HOURS"
)

