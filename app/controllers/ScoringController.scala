package controllers

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import be.objectify.deadbolt.scala.ActionBuilders
import javax.inject._
import org.wa9nnn.wfdserver.auth.SubjectAccess
import org.wa9nnn.wfdserver.bulkloader.StatusRequest
import org.wa9nnn.wfdserver.db.DBRouter
import org.wa9nnn.wfdserver.htmlTable.Table
import org.wa9nnn.wfdserver.scoring.{ScoringStatus, StartScoringRequest}
import org.wa9nnn.wfdserver.util.JsonLogging
import play.api.mvc.{Action, _}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

@Singleton
class ScoringController @Inject()(cc: ControllerComponents,
                                  actionBuilder: ActionBuilders,
                                  db: DBRouter,
                                  @Named("scoring") scoringActor: ActorRef)(implicit exec: ExecutionContext)
  extends AbstractController(cc) with JsonLogging with play.api.i18n.I18nSupport with SubjectAccess {
  implicit val timeout: Timeout = Timeout(5 seconds) // needed for `?` below

  def start(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() { implicit request =>

    scoringActor ! StartScoringRequest(subject)

    Future(Redirect(routes.ScoringController.status()))
  }

  def status(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() { implicit request =>

    (scoringActor ? StatusRequest).mapTo[ScoringStatus] map { scoringStatus: ScoringStatus =>

      Ok(views.html.scoring(scoringStatus))
    }
  }

  def viewScores(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() { implicit request =>

    db.getScores().map { records =>
      val rows = records.map(_.toRow)
      val table = Table(ScoreRecord.header, rows).withCssClass("resultTable")
      Ok(views.html.scores(table))
    }
  }
}

