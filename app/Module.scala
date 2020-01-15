
import java.nio.file.Paths

import akka.actor.{ActorRef, ActorSystem, Props}
import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.filters.AuthorizedRoutes
import com.github.racc.tscg.{TypesafeConfig, TypesafeConfigModule}
import com.google.inject.{AbstractModule, Injector, Provides}
import com.typesafe.config.Config
import javax.inject.{Named, Singleton}
import net.codingwell.scalaguice.ScalaModule
import org.wa9nnn.wfdserver.Loader
import org.wa9nnn.wfdserver.actor.GuiceActorCreator
import org.wa9nnn.wfdserver.auth.{WFDAuthorizedRoutes, WfdHandlerCache}
import org.wa9nnn.wfdserver.bulkloader.{BulkLoader, BulkLoaderTask}
import org.wa9nnn.wfdserver.contest.SubmissionControlDao
import play.api.{Configuration, Environment}

class Module(environment: Environment, configuration: Configuration) extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    install(TypesafeConfigModule.fromConfigWithPackage(configuration.underlying, "org.wa9nnn.wfdserver"))

    bind[AuthorizedRoutes].to[WFDAuthorizedRoutes]
    bind[HandlerCache].to[WfdHandlerCache]
  }

  @Provides
  @Singleton
  @Named("bulkLoader")
  def provideBulkLoader(guiceActorCreator: GuiceActorCreator, system: ActorSystem): ActorRef = {
    system.actorOf( Props(new BulkLoader(guiceActorCreator)))
  }
//  @Provides
//  @Singleton
//  @Named("bulkLoaderTask")
//  def provideBulkLoaderTask(loader: Loader, config:Config, system: ActorSystem): ActorRef = {
//    val dir = config.getString("wfd.bulkLoad.directory")
//    val dirPath = Paths.get(dir)
//
//    system.actorOf( Props(new BulkLoaderTask(loader, dirPath)))
//  }
}

