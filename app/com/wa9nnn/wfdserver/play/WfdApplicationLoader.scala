
package com.wa9nnn.wfdserver.play

import java.nio.file.{Files, Path, Paths}

import com.typesafe.config.{Config, ConfigFactory, ConfigObject, ConfigValue, ConfigValueFactory}
import play.ApplicationLoader
import play.api.inject.guice.GuiceableModule
import play.inject.guice.GuiceApplicationBuilder
import play.inject.guice.GuiceApplicationLoader

import scala.reflect.internal.FatalError
import com.wa9nnn.wfdserver.play.WfdConfigFinder._
class WfdApplicationLoader extends GuiceApplicationLoader {
  override def builder(context: ApplicationLoader.Context): GuiceApplicationBuilder = {

    val extra = wfdConfig.resolve()
    val modules: Array[GuiceableModule] = overrides(context)
    initialBuilder.in(context.environment)
      .loadConfig(extra.withFallback(context.initialConfig))
      .overrides(modules: _*)
  }

}

object WfdConfigFinder {
  val userDir: Path = Paths.get(System.getProperty("user.dir"))

  val wfdVarDir: Path = wfdVarFinder

  System.setProperty("wfdLogdir", wfdVarDir.resolve("logs").toString)


  if (Files.notExists(wfdVarDir)) {
    throw new Error(
      """Could not find a wfdVar directory! Can't continue!
        |Either create a wfdVar directory at the same level as where the wfd server is being executed from or
        |add a command line option -DwfdVarDir= <somePath>
        |""".stripMargin)
  }


  def wfdConfig: Config = {
    try {
      val conFile: Path = wfdVarDir.resolve("wfd.conf")


      if (Files.exists(conFile)) {
        val baseWithwfdVarDir = ConfigFactory.empty().withValue("wfd.varDirectory", ConfigValueFactory.fromAnyRef(wfdVarDir.toString)).resolve()

        val wfdConfig = baseWithwfdVarDir.withFallback(ConfigFactory.parseFile(conFile.toFile))
        wfdConfig
      } else {
        println("Could not find a wfd.conf")
        ConfigFactory.empty()
      }
    } catch {
      case e: Exception =>
        println(s"No wfd.conf because: ${e.getMessage}")
        ConfigFactory.empty()
    }
  }

  private def wfdVarFinder: Path = {
    Option(System.getProperty("wfdVarDir"))
      .map(Paths.get(_))
      .getOrElse {
        val parent = userDir.getParent
        parent.resolve("wfdvar")
      }
  }
}
