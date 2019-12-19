/*
 * Copyright (c) 2018 HERE. All rights reserved.
 */

package org.wa9nnn.cabrilloserver.ui

import play.twirl.api.Html

/**
  * Render a [[UiTable]] in a [[TableCell]]
  */
object TableCellTable {
  def apply(uiTable: UiTable):TableCell = {
    val render: Html = views.html.renderTable.render(uiTable)
    val t = render.body
    TableCell.rawHtml(t)
  }
}
