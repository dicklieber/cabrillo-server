
package org.wa9nnn.wfdserver.db.mysql

import org.wa9nnn.wfdserver.util.BiMap

object Operator extends BiMap[Int, String](
  0 -> "N/A",
  1 -> "CW",
  2 -> "DIGI",
  3 -> "FM",
  4 -> "RTTY",
  5 -> "SSB",
  6 -> "MIXED"

)



