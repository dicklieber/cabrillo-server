package org.wa9nnn.wfdserver.htmlTable

import play.twirl.api.Html

/**
  * Render a [[Table]] in a [[Cell]]
  */
object TableInACell {
  def apply(uiTable: Table):Cell = {
    val render: Html = views.html.renderTable.render(uiTable)
    val t = render.body
    Cell.rawHtml(t)
  }
}
