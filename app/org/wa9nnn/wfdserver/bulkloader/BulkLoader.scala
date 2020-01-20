
package org.wa9nnn.wfdserver.bulkloader

import java.time.{Duration, Instant}

import akka.actor.Actor
import javax.inject.{Inject, Named, Singleton}
import nl.grons.metrics4.scala.DefaultInstrumented
import org.wa9nnn.wfdserver.actor.GuiceActorCreator
import org.wa9nnn.wfdserver.bulkloader.BulkLoader.taskActorName
import org.wa9nnn.wfdserver.htmlTable.{Header, RowsSource, Table}
import org.wa9nnn.wfdserver.model.LogInstance
import org.wa9nnn.wfdserver.util.JsonLogging

/**
 * Actual work is delegated to [[BulkLoaderTask]] actor.
 */
class BulkLoader (guiceActorCreator: GuiceActorCreator) extends Actor with DefaultInstrumented with JsonLogging {

  private var bulkLoaderStatus: BuildLoadStatus = BuildLoadStatus()

  override def receive: Receive = {
    case StartBulkLoadRequest =>
      context.child(taskActorName).map(_ =>
        logger.error(s"Bulkload task already running!"))
        .orElse {
          start()
          None
        }

    case bs:StartingBulkLoad=>
      bulkLoaderStatus = BuildLoadStatus(started = Instant.now(), nFiles = bs.nFiles)

    case logInstance: LogInstance =>
      bulkLoaderStatus = bulkLoaderStatus.success(logInstance)
    case FailedOne =>
      bulkLoaderStatus = bulkLoaderStatus.failure()

    case StatusRequest =>
      sender ! bulkLoaderStatus
    case BulkLoadDone =>
      bulkLoaderStatus = bulkLoaderStatus.finish
    case x =>
      logger.error(s"unexpected message: $x")
  }

  private def start(): Unit = {

    val bulkLoaderTask = context.actorOf(guiceActorCreator[BulkLoaderTask]())

    bulkLoaderTask ! StartBulkLoadRequest
    bulkLoaderTask ! StartBulkLoadRequest
  }
}

object BulkLoader {
  val taskActorName: String = "bulkLoaderTask"
}

/**
 *
 * @param started       when bu;k load started.
 * @param nFiles        how files in source directory.
 * @param successSoFar  files successfully completed.
 * @param qsoCountSoFar number of QSOs in those successes.
 * @param failSoFar     number of failures.
 */
case class BuildLoadStatus(started: Instant = Instant.EPOCH,
                           nFiles: Int = 0,
                           successSoFar: Int = 0,
                           qsoCountSoFar: Int = 0,
                           failSoFar: Int = 0,
                           duration:Option[Duration] = None,
                           finished: Option[Instant] = None,
                          ) extends RowsSource{
  def finish: BuildLoadStatus = {
    val doneStamp = Instant.now()
    copy(finished = Some(doneStamp), duration = Some(Duration.between(started, doneStamp)))
  }

  def success(logInstance: LogInstance): BuildLoadStatus = {
    copy(successSoFar = successSoFar + 1, qsoCountSoFar = qsoCountSoFar + logInstance.qsoCount)
  }

  def failure(): BuildLoadStatus = {
    copy(failSoFar = failSoFar + 1)
  }
  def isRunning:Boolean = {
    started != Instant.EPOCH  && finished.isEmpty
  }
  def hasRun:Boolean = started != Instant.EPOCH
  def progress :Int = successSoFar + failSoFar
  def table:Table = Table(Header("Bulk Load Status", "Item", "Value"), toRows(includeNone = false))

}

case object StatusRequest
