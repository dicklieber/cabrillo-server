
package org.wa9nnn.wfdserver.db.mysql
import com.mysql
object WFDCodeGen {
  /**
   * Once this is run the file in wfd_gen/.../Tables.scala needs to be manually copied to app/org/wa9nnn/wfdserver/db/mysql/Tables.scala
   */
  def XXXmain(args: Array[String]): Unit = { // remove XXX to use, not having main keeps sbt native packager from generating app for this.
    slick.codegen.SourceCodeGenerator.main(
       Array(
         "slick.jdbc.MySQLProfile", // kind of SQL
         "com.mysql.cj.jdbc.Driver", // jdbc driver class
         "jdbc:mysql://WFD:[password]>>@localhost/WFD", // URL to database
         "wfd_gen", // directory where to write Tables file.
         "org.wa9nnn.wfdserver.db.mysql" // java package to use in generated file.
       )
    )

  }
}
