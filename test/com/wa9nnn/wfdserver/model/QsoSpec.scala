package com.wa9nnn.wfdserver.model

import java.time.Instant

import org.specs2.mutable.Specification

import scala.collection.concurrent.TrieMap

class QsoSpec extends Specification {
  "Qso" >> {
    "Keys" >> {
      val ourExchange = Exchange("WA9NNN", "2I IL")
      val otherExchange = Exchange("KD9BYW", "1O IL")
      val band = "20M"
      val mode = "CW"
      val ourQso = Qso(band, mode, ourExchange, otherExchange)
      val otherQso = ourQso.swap

      val map = new TrieMap[QsoKey, Qso]
      map.addOne(ourQso.receivedKey -> ourQso)
      map.addOne(otherQso.receivedKey -> otherQso)

      val maybeQso = map.remove(ourQso.sentKey)
      maybeQso must beSome(otherQso)





    }
  }
}
