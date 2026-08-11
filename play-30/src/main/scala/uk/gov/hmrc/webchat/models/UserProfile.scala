/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.webchat.models

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.auth.core.retrieve.ItmpName
import uk.gov.hmrc.auth.core.{ConfidenceLevel, Enrolment}


case class UserProfile (sessionId: String,
                        enrolments: Seq[UserEnrolment],
                        confidenceLevel: ConfidenceLevel
                        //itmpName: Option[ItmpName]
                       ) {

  def toLogString: String = enrolments.map { enrolment =>
      val identifiers = enrolment.identifiers
        .map { case (key, value) => s"$key=$value" }
        .mkString(",")

      s"service=${enrolment.serviceName}, state=${enrolment.state}, identifiers=[$identifiers]"
    }
    .mkString(";")

}

case class UserEnrolment(serviceName: String, state: String, identifiers: Map[String, String])

object UserEnrolment {
  implicit val format: OFormat[UserEnrolment] = Json.format[UserEnrolment]
}

object UserProfile {

  //implicit val itmpNameFormat: OFormat[ItmpName] = Json.format[ItmpName]
  implicit val format: OFormat[UserProfile] = Json.format[UserProfile]

  def from(enrolments: Set[Enrolment], sessionId: String, confidenceLevel: ConfidenceLevel): UserProfile = UserProfile(
    sessionId = sessionId,
    enrolments.toSeq.map { enrolment =>
      UserEnrolment(serviceName = enrolment.key,
        state = enrolment.state,
        identifiers = enrolment.identifiers.map(identifier => identifier.key -> identifier.value).toMap)
    },
    confidenceLevel = confidenceLevel
    //itmpName = itmpName  (TO DO: Get confirmation on name)
  )
}

