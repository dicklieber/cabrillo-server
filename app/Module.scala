
import java.nio.file.Paths

import akka.actor.{ActorRef, ActorSystem}
import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.filters.AuthorizedRoutes
import com.google.inject.{AbstractModule, Provides}
import com.typesafe.config.Config
import javax.inject.Singleton
import net.codingwell.scalaguice.ScalaModule
import org.wa9nnn.wfdserver.Loader
import org.wa9nnn.wfdserver.actor.BulkLoaderActorAnno
import org.wa9nnn.wfdserver.auth.{Credentials, WFDAuthorizedRoutes, WfdHandlerCache}

class Module extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[AuthorizedRoutes].to[WFDAuthorizedRoutes]
    bind[HandlerCache].to[WfdHandlerCache]
  }


  @Provides
  @Singleton
  def provideCredentials(configuration: Config): Credentials = {
    new Credentials(Paths.get(configuration.getString("wfd.credentials.file")))
  }

  @Provides
  @Singleton
  @BulkLoaderActorAnno
  def provideBulkLoader(loader: Loader, config: Config, system: ActorSystem): ActorRef = {
    system.actorOf(org.wa9nnn.wfdserver.BulkLoader.props(loader, config))
  }
}

