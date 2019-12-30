
import java.nio.file.Paths

import javax.inject.Singleton
import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.filters.AuthorizedRoutes
import com.google.inject.{AbstractModule, Provider, Provides}
import com.typesafe.config.Config
import net.codingwell.scalaguice.ScalaModule
import org.wa9nnn.wfdserver.auth.{Credentials, WFDAuthorizedRoutes, WfdHandlerCache}
import play.api.Configuration

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
}