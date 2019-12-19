
package org.wa9nnn.cabrilloserver.db.mysql

import slick.jdbc.MySQLProfile
import slick.jdbc.MySQLProfile.api._
//import slick.driver.MySQLDriver.api._

trait Database {
  val db: MySQLProfile.backend.Database = Database.forConfig(path = "wfdmysql")
}