package org.wa9nnn.wfdserver.actor

import java.lang.annotation.Annotation
import java.lang.reflect.{Constructor, Parameter}

import akka.actor.{Actor, IndirectActorProducer}
import com.google.inject.{Injector, Key}
import org.wa9nnn.wfdserver.util.JsonLogging


/**
 * This will be instantiated by Akka with the args via [[GuiceActorProducer]] generated Props.
 * @param actorClz what needs to be instantiated.
 * @param injector so we can dynamically lookup injectables.
 * @param callerArgs zero or more caller-supplied arguments.
 * @tparam A Specfic class of actor.
 */
class GuiceActorProducer[A <: Actor](actorClz: Class[A], injector: Injector, callerArgs: Any*)
  extends IndirectActorProducer with JsonLogging {
  override def actorClass: Class[_ <: Actor] = actorClz

  def injectedArgs(parameters: Array[Parameter]): Seq[Any] = {
    parameters.map(param => {
      try {
        val annotations: Array[Annotation] = param.getAnnotations
        if (annotations.length == 1) {
          injector.getInstance(Key.get(param.getType, annotations(0)))
        }
        else {
          injector.getInstance(param.getType)
        }
      }
      catch {
        case e: Exception =>
          logger.error(s"InjecectedArgs", e)
          None
      }
    }
    ).toSeq
  }

  override def produce(): Actor = {
    if (callerArgs.isEmpty) {
      injector.getInstance(actorClass)
    }
    else {
      val declaredConstructors: Array[Constructor[_]] = actorClz.getDeclaredConstructors
      if (declaredConstructors.length != 1) {
        throw new IllegalArgumentException("Actor must have exactly one constructor!")
      }
      val constructor: Constructor[_] = declaredConstructors(0)

      val parameters = constructor.getParameters
      val guiceArgs = injectedArgs(parameters.drop(callerArgs.size))
      val argsToPass: Seq[AnyRef] = (callerArgs ++ guiceArgs).toList.asInstanceOf[List[AnyRef]]
      try {
        val instance = constructor.newInstance(argsToPass: _*).asInstanceOf[Actor] // :_* i the scala "splat" operator; converts a collection to args
        logger.debug(s"created: ${instance.getClass.getName}")
        instance
      }
      catch {
        case e: Exception =>
          logger.error(
            s"""Producing actor for ${actorClz.toString} ${e.getMessage}
                |Supplied Parameters:
                |${parameters.map(p => s"${p.getName}: ${p.getType.getName}").mkString("\n")}
                |All args:
                |${argsToPass.map(a => s"${a.toString}").mkString("\n")}
             """.stripMargin, e)
          throw e
      }
    }
  }
}







