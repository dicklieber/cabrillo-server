
package org.wa9nnn.wfdserver.db.mysql

import org.wa9nnn.wfdserver.util.BiMap

object Power extends BiMap[Int, String](
  0 -> "ALL",
  1 -> "HIGH",
  2 -> "LOW",
  3 -> "QRP")



