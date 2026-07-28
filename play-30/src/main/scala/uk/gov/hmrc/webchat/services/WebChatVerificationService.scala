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

import play.api.Logging
import play.api.mvc.Request
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import uk.gov.hmrc.webchat.connectors.VerificationConnector
import uk.gov.hmrc.webchat.models.UserProfile
import uk.gov.hmrc.webchat.models.verificationservice.UserVerificationRequest

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class WebChatVerificationService @Inject()(
                                            val authConnector: AuthConnector,
                                            verificationConnector: VerificationConnector
                                          )(implicit ec: ExecutionContext)
  extends uk.gov.hmrc.auth.core.AuthorisedFunctions
    with Logging {


  def verifyUser()(implicit request: Request[_]): Future[Unit] = {

    implicit val hc: HeaderCarrier =
      HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    logger.info("Starting webchat authentication")

    authorised()
      .retrieve(Retrievals.allEnrolments) {
        enrolments =>
          val profile = UserProfile.from(enrolments.enrolments)

          logger.info(s"Retrieved profile: ${profile.toLogString}"
          )

          val verificationRequest = UserVerificationRequest(profile)

          verificationConnector
            .sendVerificationDetails(verificationRequest)
            .map { response =>
              logger.info(s"Verification response: ${response.status}"
              )
            }
      }
      .recover {
        // Identity data unavailable / user not logged in
        case ex: uk.gov.hmrc.auth.core.MissingBearerToken =>
          logger.error("Authentication failed: Missing bearer token (identity unavailable)", ex)

        // Session mismatch / authorisation failure
        case ex: uk.gov.hmrc.auth.core.AuthorisationException =>
          logger.error("Authentication failed: Authorisation failed or session mismatch", ex)

        // Timeout
        case ex: java.util.concurrent.TimeoutException =>
          logger.error("Authentication failed: Identity service timed out", ex)

        // Upstream auth/API failure
        case ex: uk.gov.hmrc.http.UpstreamErrorResponse =>
          logger.error(
            s"Authentication failed: Upstream auth service returned ${ex.statusCode}", ex)

        // Any other unexpected failure
        case ex =>
          logger.error("Authentication failed: Unexpected error retrieving identity", ex)
      }
  }
}