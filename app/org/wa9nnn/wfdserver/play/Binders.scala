
package org.wa9nnn.wfdserver.play

import org.wa9nnn.wfdserver.CallSignId
import play.api.mvc.QueryStringBindable

import scala.util.control.Exception._

object Binders {
  implicit def callSignIdQueryStringBinder: QueryStringBindable[CallSignId] = new QueryStringBindable[CallSignId] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, CallSignId]] = {
      allCatch opt  Right(CallSignId(params(key).head))
    }
    override def unbind(key: String, callsignId: CallSignId): String = {
      s"csi=$callsignId"
    }
  }
}
