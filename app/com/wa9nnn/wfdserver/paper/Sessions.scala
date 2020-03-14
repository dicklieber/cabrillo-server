
package com.wa9nnn.wfdserver.paper

import akka.actor.Actor
import com.wa9nnn.wfdserver.auth.WfdSubject
import com.wa9nnn.wfdserver.htmlTable.{Row, Table}
import com.wa9nnn.wfdserver.model.CallSign

import scala.collection.concurrent.TrieMap

class Sessions(paperLogsDao: PaperLogsDao) extends Actor {
  private val byCallSign = new TrieMap[CallSign, SessionDao]()


  def invalidate(is:InvalidateSession): Unit = {
    byCallSign.remove(is.callSign)
  }


  def sessionListTable: Table = {
    Table(SessionDao.header(byCallSign.size), byCallSign.values.map(_.toRow).toSeq)
      .withCssClass("resultTable")
  }

  def start(startSession: StartSession): SessionDao = {
    byCallSign.remove(startSession.callSign)
    session(startSession.sessionRequest)
  }

  def session(sessionRequest: SessionRequest): SessionDao = {
    val callSign = sessionRequest.callSign
    byCallSign.getOrElseUpdate(callSign, paperLogsDao.start(callSign)(sessionRequest.subject))
  }

  override def receive: Receive = {
    case is:InvalidateSession =>
      invalidate(is)
      sender ! sessionListTable
    case SessionList =>
      sender ! sessionListTable
    case ss:StartSession =>
      sender ! start(ss)
    case s:SessionRequest =>
      sender ! session(s)
  }
}

case class InvalidateSession(callSign: CallSign)(implicit  subject: WfdSubject)

case class StartSession(callSign: CallSign, subject: WfdSubject){
  def sessionRequest:SessionRequest = SessionRequest(callSign, subject)
}
case class SessionRequest(callSign: CallSign, subject: WfdSubject)

case object SessionList