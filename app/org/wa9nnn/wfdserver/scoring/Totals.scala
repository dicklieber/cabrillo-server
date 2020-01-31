
package org.wa9nnn.wfdserver.scoring

import org.wa9nnn.wfdserver.htmlTable.Row

object AwardTotalsRow {
  /**
   *
   * @param item    row header
   * @param award   points awarded
   * @param explain details.
   */
  def apply(item: String, award: Int, explain: Any): Row = Row(item, award, "", explain)
}

object MultiplierTotalsRow {
  /**
   *
   * @param item    row header
   * @param mutiplier   points awarded
   * @param explain details.
   */
  def apply(item: String, mutiplier: Int, explain: Any): Row = Row(item, "", mutiplier, explain)
}



