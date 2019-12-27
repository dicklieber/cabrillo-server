
package org.wa9nnn.wfdserver.db.mysql

import org.wa9nnn.wfdserver.util.BiMap

object Mode extends BiMap[Int, String](
  0 -> "N/A",
  1 -> "CW",
  2 -> "DIGI",
  3 -> "FM",
  4 -> "RTTY",
  5 -> "PH", //todo was SSB in db
  6 -> "MIXED")



