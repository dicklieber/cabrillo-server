
package com.wa9nnn.wfdserver.db.mysql

import com.wa9nnn.wfdserver.util.BiMap

object OperatorType extends BiMap[Int, String](
  0 -> "N/A",
  1 -> "SINGLE-OP",
  2 -> "MULTI-OP",
  3 -> "CHECKLOG",
)



