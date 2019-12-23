
package org.wa9nnn.wfdserver.db.mysql
import com.mysql
object WFDCodeGen {
  def main(args: Array[String]): Unit = {
    slick.codegen.SourceCodeGenerator.main(
       Array("slick.jdbc.MySQLProfile", "com.mysql.cj.jdbc.Driver", "jdbc:mysql://WFD:6SJ7-GTtube@localhost/WFD", "wfd_gen", "org.wa9nnn.cabrilloserver.db.mysql")
    )

  }
}
