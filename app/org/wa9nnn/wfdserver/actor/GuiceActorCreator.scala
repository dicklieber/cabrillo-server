package org.wa9nnn.wfdserver.actor

import akka.actor.{Actor, Props}
import com.google.inject.Injector
import javax.inject.Inject

import scala.reflect._

class GuiceActorCreator @Inject()(val injector: Injector) {
  /**
   *
   * @param args zero or more args. These must be the 1st parameters of the actor. Any additional parameters will be injected by Guice.
   * @tparam A Actor class to be instantiated.
   * @return a Props that can be passed to [[akka.actor.ActorRefFactory.actorOf]]
   */
  def apply[A <: Actor : ClassTag](args: Any*): Props = {
    val producerClz = classTag[GuiceActorProducer[A]].runtimeClass
    val actorClass: Class[_] = classTag[A].runtimeClass
    Props(producerClz, actorClass, injector, args)
  }
}
