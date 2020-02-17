
package com.wa9nnn.wfdserver.play



 package object FieldConstructor {
  import views.html.helper.FieldConstructor
  implicit val fieldConstructor =  FieldConstructor (views.html.fieldConstructor.f)
//  implicit val fieldConstructor: FieldConstructor = new FieldConstructor {
//    def apply(elements: FieldElements): play.twirl.api.Html = {
//      html.fieldConstructor(elements)
//    }
//  }

}
