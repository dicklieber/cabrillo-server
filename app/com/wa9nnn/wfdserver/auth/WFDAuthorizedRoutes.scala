
package com.wa9nnn.wfdserver.auth

import be.objectify.deadbolt.scala.allOfGroup
import be.objectify.deadbolt.scala.filters.{AuthorizedRoute, AuthorizedRoutes, FilterConstraints, _}
import javax.inject.Inject

class WFDAuthorizedRoutes @Inject()(filterConstraints: FilterConstraints) extends AuthorizedRoutes {
  override val routes: Seq[AuthorizedRoute] = Seq(
    AuthorizedRoute(Get, "/netcontrol", filterConstraints.subjectPresent, handler = None),
    AuthorizedRoute(Any, "/view/$foo<[^/]+>/$bar<[^/]+>", filterConstraints.restrict(allOfGroup("someRole")), handler = None)
  )

}
