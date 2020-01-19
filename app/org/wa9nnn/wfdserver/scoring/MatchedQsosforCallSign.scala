
package org.wa9nnn.wfdserver.scoring

import org.wa9nnn.wfdserver.model.WfdTypes.CallSign

case class MatchedQsosforCallSign(callSign: CallSign, qsoPairs: Seq[MatchedQso]) {

  def validate: Seq[Throwable] = {

    qsoPairs.flatMap { qsoPair =>
      try {
        qsoPair.validate(callSign)
        Seq.empty
      } catch {
        case e: Exception =>
          Seq(e)
      }
    }
  }
}
