
package com.wa9nnn.wfdserver.db.mysql

import com.wa9nnn.wfdserver.util.BiMap

object Assisted extends BiMap[Boolean, String](
  true -> "ASSISTED",
  false -> "NON-ASSISTED"
)

