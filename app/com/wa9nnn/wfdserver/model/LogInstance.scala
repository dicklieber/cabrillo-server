
package com.wa9nnn.wfdserver.model


/**
 * Object persisted in MongoDB and used for scoring.
 *
 * @param _id        if mongo this is ObjectId().toHexString, if MySQL it's the PK int value as a String.
 * @param qsoCount   continence to get count of all qsos without having to traverse deeper into the object.
 * @param stationLog everything from cabrillo except the QSOs
 * @param qsos       just the QSOs
 */
case class LogInstance(_id: String,
                       qsoCount: Int,
                       stationLog: StationLog,
                       qsos: Seq[Qso]
                      ) {
  def id: String = _id

  def callSign: CallSign = stationLog.callSign
}
