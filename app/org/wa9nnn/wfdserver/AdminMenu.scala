
package org.wa9nnn.wfdserver

import _root_.play.api.mvc.Call
import controllers.routes

object AdminMenu {
  val menuItems: Seq[MenuItem] = Seq(
    MenuItem(routes.SubmissionControlController.index(), "Submission Control", """Control when submission are allowed and messages before, during and after."""),
    MenuItem(routes.SearchController.index(), "Search by CallSign", """Find submissions by search on partial callSign"""),
    MenuItem(routes.AdminController.callSigns(), "All submissions", """View all submissions."""),
    MenuItem(routes.AdminController.recent(), "Recent", """Last 25 submissions."""),
    MenuItem(routes.StatsController.index(), """Statistics""", """Various aggregate information."""),
    MenuItem(routes.UserController.index(), """User Manager""", """Edit who has access to the admin pages."""),
    MenuItem(routes.BulkLoaderController.status(), "Bulk loader", """Bulk load from a directory accessible from the server. Actual direction is configured in application.conf: wfd.bulkLoad.directory"""),
    MenuItem(routes.MetricsController.metrics(), "Metrics", """Counter, meters, times and JVM stats.""")
  )
}

case class MenuItem(call: Call, button: String, help: String)