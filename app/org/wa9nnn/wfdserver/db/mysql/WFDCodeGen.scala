
package org.wa9nnn.wfdserver.db.mysql
import com.mysql
object WFDCodeGen {
  def main(args: Array[String]): Unit = {
    slick.codegen.SourceCodeGenerator.main(
       Array("slick.jdbc.MySQLProfile", "com.mysql.cj.jdbc.Driver", "jdbc:mysql://WFD:12AX7A-6146@localhost/WFD", "wfd_gen", "org.wa9nnn.cabrilloserver.db.mysql")
    )

  }
}
