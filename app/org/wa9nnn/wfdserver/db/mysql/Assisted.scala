
package org.wa9nnn.wfdserver.db.mysql

import org.wa9nnn.wfdserver.util.BiMap

object Assisted extends BiMap[Boolean, String](
  true -> "ASSISTED",
  false -> "NON-ASSISTED"
)

