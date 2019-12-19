package org.wa9nnn.cabrilloserver.ui

import java.io.{PrintWriter, StringWriter}

object ExceptionUi {
  def render(exception: Exception):String = {
    val stringWriter: StringWriter = new StringWriter()
    val writer:PrintWriter = new PrintWriter(stringWriter)
    exception.printStackTrace(writer)
    writer.close()
    stringWriter.toString
  }
}
