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

package uk.gov.hmrc.webchat.controllers

import play.api.Logging
import play.api.mvc.{Request}
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals

import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.webchat.models.UserProfile

import javax.inject.Inject;

class AuthFunction @Inject() (override val authConnector: AuthConnector)
                             (implicit executionContext: ExecutionContext) extends AuthorisedFunctions with Logging {

   def getEnrolments(implicit request: Request[_], hc: HeaderCarrier): Future[UserProfile] = {
    logger.debug("[AuthFunction][getEnrolments] retrieve enrolments")

    authorised()
      .retrieve(Retrievals.allEnrolments) {
        case allEnrolments =>
          Future.successful(UserProfile(allEnrolments))
      }
  }
}