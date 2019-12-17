
package org.wa9nnn.cabrilloserver.db.mysql

import org.wa9nnn.cabrillo.model.CabrilloData

object Address {
  def apply(cd: CabrilloData): Option[String] = {
    Some(cd("ADDRESS").mkString("\n").take(75))
  }
}
