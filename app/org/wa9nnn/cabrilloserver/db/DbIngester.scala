
package org.wa9nnn.cabrilloserver.db
import org.wa9nnn.cabrillo.model._
/**
 * Something that can consume a valid [[CabrilloData]]
 */
trait DbIngester {
def apply(cabrilloData: CabrilloData):Unit
}
