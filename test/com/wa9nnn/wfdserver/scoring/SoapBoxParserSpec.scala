package com.wa9nnn.wfdserver.scoring

import org.specs2.matcher.DataTables
import org.specs2.mutable.Specification

class SoapBoxParserSpec extends Specification with DataTables {

  "SoapBoxParser" should {
    "ok" in {
      val body = "30000 points for not using commercial power."
      val maybeTuple = SoapBoxParser(body)
      maybeTuple must beEqualTo(SoapBoxAward("power", body, None, 30000))
    }
    "Tests" in {
      "SoapBox" | "expected" |
        "1,500 points for not using commercial power." !! SoapBoxAward("power", "1,500 points for not using commercial power.",None, 1500, 1500) |
        "1,500 points for satellite QSO(s)." !! SoapBoxAward("satellite","1,500 points for satellite QSO(s).") |
        "1,500 points for setting up outdoors." !! SoapBoxAward("outdoors", "1,500 points for setting up outdoors.") |
        "1,500 points for setting up away from home." !! SoapBoxAward("home", "1,500 points for setting up away from home.") |
        "crap" !! SoapBoxAward("??", "crap", Some("No parse!"), 0, 0) |
        "BONUS 6000" !! SoapBoxAward("??", "BONUS 6000", Some("No parse!"), 0, 0) |
        "1,500 points for not using commercial power." !! SoapBoxAward("power", "1,500 points for not using commercial power.") |> { (soapBox: String, expected: SoapBoxAward) =>
        SoapBoxParser(soapBox) must beEqualTo(expected)
      }
    }
  }
}