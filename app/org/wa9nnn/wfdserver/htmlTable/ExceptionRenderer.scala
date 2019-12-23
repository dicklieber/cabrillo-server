package org.wa9nnn.wfdserver.htmlTable

import java.io.{PrintWriter, StringWriter}

object ExceptionRenderer {
  def apply(exception: Exception):String = {
    val stringWriter: StringWriter = new StringWriter()
    val writer:PrintWriter = new PrintWriter(stringWriter)
    exception.printStackTrace(writer)
    writer.close()
    stringWriter.toString
  }
}
