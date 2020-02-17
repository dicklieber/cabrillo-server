
package com.wa9nnn.wfdserver.util

import java.nio.file.{Files, Path}

import play.api.libs.json.{JsValue, Json}

import scala.util.Using

abstract class PersistableJson[T](filePath: Path) extends JsonLogging {
  def set(v: JsValue): Unit

  def readFile(): Unit = {
    if (Files.exists(filePath)) {
      Using(Files.newInputStream(filePath)) { inputStream =>
        set(Json.parse(inputStream))
        logger.info(s"Loaded submissionControl from $filePath")
      }
    } else {
      logger.error(s"Did not find file at $filePath, using default.")
    }
  }

  def saveAndRead(jsValue: JsValue): Unit = {
    val sJson = Json.prettyPrint(jsValue)
    SaveWithBackup(filePath, sJson)
    readFile()
  }

}
