
package com.wa9nnn.wfdserver.play

import views.html
import views.html.helper.{FieldConstructor, FieldElements}



package object WfdFieldConstructor
{
  implicit val fieldConstructor: FieldConstructor = new FieldConstructor {
    def apply(elements: FieldElements): play.twirl.api.Html = {
      html.wfdFieldConstructor(elements)
    }
  }

}

package object WfdLabelAbove
{
  implicit val fieldConstructor: FieldConstructor = new FieldConstructor {
    def apply(elements: FieldElements): play.twirl.api.Html = {
      html.wfdLabelAbove(elements)
    }
  }

}
