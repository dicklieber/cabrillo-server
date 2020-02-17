
package com.wa9nnn.wfdserver.contest

import java.nio.file.{Path, Paths}
import java.time.{Clock, Instant}
import com.wa9nnn.wfdserver.contest.Message._

import com.github.racc.tscg.TypesafeConfig
import javax.inject.{Inject, Singleton}
import com.wa9nnn.wfdserver.util.PersistableJson
import play.api.libs.json.{JsValue, Json}

/**
 * Determine if submissions are currently being accepted and text to display before, during and after submission time.
 */
@Singleton
class SubmissionControlDao(submissionControlConfigPath: Path, clock: Clock) extends PersistableJson[SubmissionConfig](submissionControlConfigPath) {
  @Inject() def this(@TypesafeConfig("wfd.submissionControl") submissionControlConfigPath: String) = {
    this(Paths.get(submissionControlConfigPath), Clock.systemUTC())
  }

  private var submissionConfig: SubmissionConfig = SubmissionConfig.default
  readFile()

  def current(): CurrentSubmissionState = {
    if (Instant.now.isBefore(submissionConfig.times.submissionBegin)) {
      CurrentSubmissionState(submissionsAllowed = false, submissionConfig.beforeMessage, submissionConfig.times.display)
    } else if (Instant.now.isBefore(submissionConfig.times.submissionEnd)) {
      CurrentSubmissionState(submissionsAllowed = true, submissionConfig.duringMessage, submissionConfig.times.display)
    } else {
      CurrentSubmissionState(submissionsAllowed = false, submissionConfig.afterMessage, submissionConfig.times.display)
    }
  }

  def get: SubmissionConfig = {
    submissionConfig
  }

  def put(submissionConfig: SubmissionConfig): Unit = {

    saveAndRead(Json.toJson(submissionConfig))
  }

  override def set(v: JsValue): Unit = {
    submissionConfig = v.as[SubmissionConfig]
  }
}
