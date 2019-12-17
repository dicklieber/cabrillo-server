package org.wa9nnn.cabrilloserver.db.mysql

import java.util.NoSuchElementException

import org.specs2.mutable.Specification

class BiMapSpec extends Specification {


  private val biMap: BiMap[Int, String] = new BiMap[Int, String](
    0 -> "ALL",
    1 -> "160M",
    2 -> "80M",
    3 -> "40M")

  "BiMapSpec" >> {
    "forward" >> {
      biMap(2) must beEqualTo("80M")
    }
    "forward missing" >> {
      biMap(200) must throwA[NoSuchElementException]
    }

    "opt"  >> {
      "ok" >> {
        biMap(Some("80M")) must beSome(2)
      }
      "missing" >> {
        biMap(Some("Crap")) must beNone
      }
      "already None" >> {
        biMap(None) must beNone
      }
    }

    "reverse" >> {
      biMap("80M") must beEqualTo(2)
    }
    "domain" >> {
      biMap.domain must beEqualTo(Set(0, 1, 2, 3))
    }
    "codomain" >> {
      biMap.codomain must beEqualTo(Set("ALL", "160M", "80M", "40M"))
    }
  }
}
