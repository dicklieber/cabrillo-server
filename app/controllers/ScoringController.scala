package controllers

import java.io.StringWriter

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import be.objectify.deadbolt.scala.{ActionBuilders, AuthenticatedRequest}
import com.opencsv.CSVWriter
import com.typesafe.config.Config
import javax.inject._
import com.wa9nnn.wfdserver.auth.SubjectAccess
import com.wa9nnn.wfdserver.bulkloader.StatusRequest
import com.wa9nnn.wfdserver.db.ScoreFilter._
import com.wa9nnn.wfdserver.db.{DBRouter, ScoreFilter}
import com.wa9nnn.wfdserver.htmlTable.{Row, Table}
import com.wa9nnn.wfdserver.scoring.{ScoreRecord, ScoringStatus, StartScoringRequest}
import com.wa9nnn.wfdserver.util.JsonLogging
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, _}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Try, Using}

@Singleton
class ScoringController @Inject()(cc: ControllerComponents,
                                  actionBuilder: ActionBuilders,
                                  db: DBRouter,
                                  @Named("scoring") scoringActor: ActorRef)(implicit exec: ExecutionContext, config: Config)
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

  private val scoreFilterForm: Form[ScoreFilter] = Form(
    mapping(
      "category" -> optional(text),
      "section" -> optional(text),
      "includeErrantDetail" -> boolean,
      "submit" -> text
    )(ScoreFilter.apply)(ScoreFilter.unapply)
  )

  def viewScores(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() { implicit request =>

    val scoreFilter: Form[ScoreFilter] = scoreFilterForm.fill(ScoreFilter())

    showScores(scoreFilter)
  }


  def viewFiteredScores(): Action[AnyContent] = actionBuilder.SubjectPresentAction().defaultHandler() { implicit request =>

    val ff: Form[ScoreFilter] = scoreFilterForm.bindFromRequest()


    showScores(ff)
  }

  def showScores(scoreFilterForm: Form[ScoreFilter])(implicit request: AuthenticatedRequest[AnyContent]): Future[Result] = {

    val scoreFilter = scoreFilterForm.get

    if (scoreFilter.submit == "All") {
      sendScore(scoreFilter, scoreFilterForm.fill(ScoreFilter(includeErrantDetail = scoreFilter.includeErrantDetail)))
    } else if (scoreFilter.submit.startsWith("Download")) {
      sendCsv(scoreFilter)
    } else {
      sendScore(scoreFilter, scoreFilterForm)
    }
  }

  def sendCsv(scoreFilter: ScoreFilter)(implicit request: AuthenticatedRequest[AnyContent]): Future[Result] = {
    db.getScores(scoreFilter).map { scoreRrecords =>
      val stringOrError: Try[String] = Using(new StringWriter()) { sw =>
        val writer = new CSVWriter(sw)
        writer.writeNext(Array("CallSign", "Category", "Section", "OverallRank", "CategoryRank", "AwardedScore", "ClaimedScore", "ErrantQSOs"))
        scoreRrecords.foreach { sr =>
          writer.writeNext(sr.toCsv)
        }
        writer.flush()
        sw.toString
      }

      Ok(stringOrError.get).withHeaders(
        "Content-Type" -> "text/csv",
        "Content-disposition" -> s"attachment; filename=wfdscores.csv"
      )

    }
  }

  def sendScore(scoreFilter: ScoreFilter, ff: Form[ScoreFilter])(implicit request: AuthenticatedRequest[AnyContent]):Future[Result] ={
    val categoriesBuilder = Set.newBuilder[String]
    val sectionsBuilder = Set.newBuilder[String]
    db.getScores(scoreFilter).map { records =>
      val rows: Seq[Row] = records
        .sortBy(_.awardedPoints)
        .reverse
        .map { scoreRecord =>
          val callCatSect = scoreRecord.callCatSect
          categoriesBuilder += callCatSect.category
          sectionsBuilder += callCatSect.arrlSection
          scoreRecord.toRow(scoreFilter)
        }
      val table = new Table(ScoreRecord.header(rows.length), rows)
        .withCssClass("resultTable tablesorter")
        .withId("scoresTable")
      Ok(views.html.scores(table, ff,
        categoriesBuilder.result().toSeq.sorted.prepended(chooseCategory),
        sectionsBuilder.result().toSeq.sorted.prepended(chooseSection)
      ))
    }
  }
}

