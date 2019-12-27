
package org.wa9nnn.wfdserver.db.mysql

import org.wa9nnn.wfdserver.util.BiMap

object Station extends BiMap[Int, String](
  0 -> "N/A",
  1 -> "FIXED",
  2 -> "MOBILE",
  3 -> "PORTABLE",
  4 -> "ROVER",
  5 -> "ROVER-LIMITED",
  6 -> "ROVER-UNLIMITED",
  7 -> "EXPEDITION",
  8 -> "HQ",
  9 -> "SCHOOL"

)




