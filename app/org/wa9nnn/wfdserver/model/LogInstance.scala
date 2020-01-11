
package org.wa9nnn.wfdserver.model

import org.mongodb.scala.bson.ObjectId

/**
 * Object persisted in MongoDB and used for scoring.
 *
 * @param _id        if mongo this is ObjectId().toHexString, if MySQL it's the PK int value as a String.
 * @param logVersion incremented each time this callSign's log has been replaced.
 * @param qsoCount   continence to get count of all qsos without having to traverse deeper into the object.
 * @param stationLog everything from cabrillo except the QSOs
 * @param qsos       just the QSOs
 */
case class LogInstance(_id: String = new ObjectId().toHexString,
                       logVersion: Int,
                       qsoCount: Int,
                       stationLog: StationLog,
                       qsos: Seq[Qso]
                      ) {
   def id: String = _id
}
