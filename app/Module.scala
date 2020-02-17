
import akka.actor.{ActorRef, ActorSystem, Props}
import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.filters.AuthorizedRoutes
import com.github.racc.tscg.TypesafeConfigModule
import com.google.inject.{AbstractModule, Provides}
import javax.inject.{Named, Singleton}
import net.codingwell.scalaguice.ScalaModule
import com.wa9nnn.wfdserver.actor.GuiceActorCreator
import com.wa9nnn.wfdserver.auth.{WFDAuthorizedRoutes, WfdHandlerCache}
import com.wa9nnn.wfdserver.bulkloader.BulkLoader
import com.wa9nnn.wfdserver.scoring.ScoringActor
import play.api.{Configuration, Environment}

class Module(environment: Environment, configuration: Configuration) extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    install(TypesafeConfigModule.fromConfigWithPackage(configuration.underlying, "com.wa9nnn.wfdserver"))

    bind[AuthorizedRoutes].to[WFDAuthorizedRoutes]
    bind[HandlerCache].to[WfdHandlerCache]
  }

  @Provides
  @Singleton
  @Named("bulkLoader")
  def provideBulkLoader(guiceActorCreator: GuiceActorCreator, system: ActorSystem): ActorRef = {
    system.actorOf( Props(new BulkLoader(guiceActorCreator)))
  }

 @Provides
  @Singleton
  @Named("scoring")
  def provideScoring(guiceActorCreator: GuiceActorCreator, system: ActorSystem): ActorRef = {
    system.actorOf( Props(new ScoringActor(guiceActorCreator)))
  }



}

