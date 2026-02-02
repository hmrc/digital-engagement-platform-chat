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

import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.webchat.models.UserProfile

import scala.concurrent.{ExecutionContext, Future};

trait AuthFunction extends AuthorisedFunctions {

  val authConnector: AuthConnector

   def withUserProfile[T](f: UserProfile => Future[T])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[T] = {
     authorised()
      .retrieve(Retrievals.allEnrolments) {
        allEnrolments => f(UserProfile(allEnrolments.enrolments))
      }
  }
}