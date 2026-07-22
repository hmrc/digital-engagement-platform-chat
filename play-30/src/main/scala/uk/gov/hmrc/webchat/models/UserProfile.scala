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
import uk.gov.hmrc.auth.core.Enrolment

import java.util.UUID


case class UserProfile (id: String, enrolments: Seq[UserEnrolment]) {

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

  implicit val format: OFormat[UserProfile] = Json.format[UserProfile]

  private def uuid: String = UUID.randomUUID().toString

  def from(enrolments: Set[Enrolment]): UserProfile = UserProfile(
    id = uuid,
    enrolments.toSeq.map { enrolment =>
      UserEnrolment(serviceName = enrolment.key,
        state = enrolment.state,
        identifiers = enrolment.identifiers.map(identifier => identifier.key -> identifier.value).toMap)
    }
  )
}
