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

package uk.gov.hmrc.webchat.services

import play.api.mvc.Request
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.{ItmpName, ~}

import javax.inject.Inject
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions, ConfidenceLevel}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import uk.gov.hmrc.webchat.models.UserProfile

import scala.concurrent.{ExecutionContext, Future}

class AuthUserProfileProvider @Inject()(
                                         val authConnector: AuthConnector
                                       )(implicit ec: ExecutionContext)
  extends UserProfileProvider
    with AuthorisedFunctions {

  override def retrieveUserProfile(sessionId: String)(implicit request: Request[_]): Future[UserProfile] = {
    implicit val hc: HeaderCarrier =
      HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    authorised()
      .retrieve(
        Retrievals.allEnrolments and Retrievals.confidenceLevel
        //and Retrievals.itmpName
      ) {
        case enrolments ~ (confidenceLevel: ConfidenceLevel) =>

          val profile = UserProfile.from(
            enrolments.enrolments,
            sessionId,
            confidenceLevel
            //itmpName
          )

          Future.successful(profile)
      }
  }
}