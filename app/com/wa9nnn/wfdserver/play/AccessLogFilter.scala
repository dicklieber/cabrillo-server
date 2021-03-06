package com.wa9nnn.wfdserver.play

import javax.inject.Inject
import akka.stream.Materializer
import com.wa9nnn.wfdserver.CallSignId
import com.wa9nnn.wfdserver.auth.WfdSubject
import com.wa9nnn.wfdserver.auth.WfdSubject.sessionKey
import com.wa9nnn.wfdserver.util.{JsonLogging, LogJson}
import play.api.Logging
import play.api.mvc._

import scala.util.control.Exception.{allCatch, _}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class AccessLogFilter @Inject()(implicit val mat: Materializer, ec: ExecutionContext) extends Filter with JsonLogging {
  setLoggerName("access")

  def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    val startTime = System.currentTimeMillis

    nextFilter(requestHeader).map { result =>
      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime
      val remoteAddress = requestHeader.remoteAddress
      val user = requestHeader.session.get(sessionKey) match {
        case Some(jsonWfdSubject) =>
          allCatch opt WfdSubject.fromJson(jsonWfdSubject).identifier
        case _ => "-"
      }

      logger.info(
        s"$remoteAddress - $user ${requestHeader.method} ${requestHeader.uri} took ${requestTime}ms and returned ${result.header.status}"
      )

      result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }
}