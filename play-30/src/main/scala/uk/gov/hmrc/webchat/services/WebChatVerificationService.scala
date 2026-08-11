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
import uk.gov.hmrc.auth.core.{AuthorisationException, MissingBearerToken}
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import uk.gov.hmrc.webchat.connectors.VerificationConnector
import uk.gov.hmrc.webchat.models.verificationservice.UserVerificationRequest

import java.util.concurrent.TimeoutException
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class WebChatVerificationService @Inject()(
                                            userProfileProvider: UserProfileProvider,
                                            verificationConnector: VerificationConnector
                                          )(implicit ec: ExecutionContext)
  extends Logging {


  def verifyUser(sessionId: String)(implicit request: Request[_]): Future[Unit] = {

    implicit val hc: HeaderCarrier =
      HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    logger.info("Starting webchat authentication")

    userProfileProvider
      .retrieveUserProfile(sessionId)
      .flatMap { profile =>

        logger.info(s"Retrieved profile: ${profile.toLogString}")

        verificationConnector
          .sendVerificationDetails(UserVerificationRequest(profile))
          .map { response =>
            logger.info(s"Verification response: ${response.status}")
          }
      }
      .recover {
        // Identity data unavailable / user not logged in
        case ex: MissingBearerToken =>
          logger.error("Authentication failed: Missing bearer token (identity unavailable)", ex)

        // Session mismatch / authorisation failure
        case ex: AuthorisationException =>
          logger.error("Authentication failed: Authorisation failed or session mismatch", ex)

        // Timeout
        case ex: TimeoutException =>
          logger.error("Authentication failed: Identity service timed out", ex)

        // Upstream auth/API failure
        case ex: UpstreamErrorResponse =>
          logger.error(
            s"Authentication failed: Upstream auth service returned ${ex.statusCode}", ex)

        // Any other unexpected failure
        case ex =>
          logger.error("Authentication failed: Unexpected error retrieving identity", ex)
      }
  }
}