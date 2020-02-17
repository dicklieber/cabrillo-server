
package com.wa9nnn.wfdserver.util

object MarkDown {
  import laika.api._
  import laika.{format => mdFormat}

  private val tomd = Transformer
    .from(mdFormat.Markdown)
    .to(mdFormat.HTML)
    .build

  def apply(in: play.api.data.Field):String = {
    apply(in.value.get)
  }
   def apply(in:String):String = {
    tomd.transform(in).getOrElse("in")
  }
}
