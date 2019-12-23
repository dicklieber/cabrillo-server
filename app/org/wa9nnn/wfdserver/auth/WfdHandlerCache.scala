
package org.wa9nnn.wfdserver.auth

import be.objectify.deadbolt.scala.{DeadboltHandler, HandlerKey}
import be.objectify.deadbolt.scala.cache.HandlerCache
import com.google.inject.Inject
import com.typesafe.scalalogging.LazyLogging
import javax.inject.Singleton

@Singleton
class WfdHandlerCache @Inject() extends HandlerCache with LazyLogging {
  val defaultHandler: DeadboltHandler = new WfdDeadboltHandler

  // HandlerKeys is an user-defined object, containing instances
  // of a case class that extends HandlerKey
  private val handlers: Map[Any, DeadboltHandler] = {
    Map.empty
  }
  //  Map(
  //    HandlerKeys.defaultHandler -> defaultHandler,
  //    HandlerKeys.altHandler -> new MyDeadboltHandler(Some(MyAlternativeDynamicResourceHandler)),
  //    HandlerKeys.userlessHandler -> new MyUserlessDeadboltHandler)

  // Get the default handler.
  override def apply(): DeadboltHandler =
    defaultHandler

  // Get a named handler
  override def apply(handlerKey: HandlerKey): DeadboltHandler = {
    logger.error(s"WfdHandlerCache asking for handlerKey: $handlerKey")
    handlers(handlerKey)
  }
}