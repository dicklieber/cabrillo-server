
package org.wa9nnn.wfdserver.scoring

case class MatchedQsosforCallSign(callSign: String, qsoPairs: Seq[MatchedQso]) {

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
