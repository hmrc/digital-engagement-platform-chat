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

import org.mockito.ArgumentMatchers.{any => anyArg}
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.wordspec.AnyWordSpec
import play.api.mvc.{AnyContentAsEmpty, Request}
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.ConfidenceLevel.L250
import uk.gov.hmrc.auth.core.MissingBearerToken
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, UpstreamErrorResponse}
import uk.gov.hmrc.webchat.connectors.VerificationConnector
import uk.gov.hmrc.webchat.models.{UserEnrolment, UserProfile}
import uk.gov.hmrc.webchat.models.verificationservice.UserVerificationRequest

import java.util.concurrent.TimeoutException
import scala.concurrent.{ExecutionContext, Future}

class WebChatVerificationServiceSpec
  extends AnyWordSpec
    with MockitoSugar
    with ScalaFutures
    with Matchers {

  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val hc: HeaderCarrier = HeaderCarrier()

  "verifyUser" should {

    "send verification details when user profile is retrieved" in {

      val (service, userProfileProvider, verificationConnector) = createService()

      implicit val request: Request[AnyContentAsEmpty.type] =
        FakeRequest("GET", "/")

      val profile =
        UserProfile(
          "test-user-id",
          Seq(
            UserEnrolment(
              "HMRC-MTD-IT",
              "Activated",
              Map("MTDITID" -> "12345")
            )
          ),
          confidenceLevel = L250
        )

      when(
        userProfileProvider.retrieveUserProfile(
          anyArg[String]
        )(anyArg[Request[_]])
      ).thenReturn(
        Future.successful(profile)
      )

      when(
        verificationConnector.sendVerificationDetails(
          anyArg[UserVerificationRequest]
        )(anyArg[HeaderCarrier])
      ).thenReturn(
        Future.successful(HttpResponse(200))
      )

      service.verifyUser("test-user-id").futureValue

      verify(verificationConnector).sendVerificationDetails(
        anyArg[UserVerificationRequest]
      )(anyArg[HeaderCarrier])
    }

    "not fail when authentication fails" in {

      val (service, userProfileProvider, verificationConnector) = createService()

      implicit val request: Request[AnyContentAsEmpty.type] =
        FakeRequest("GET", "/")

      when(
        userProfileProvider.retrieveUserProfile(
          anyArg[String]
        )(anyArg[Request[_]])
      ).thenReturn(
        Future.failed(MissingBearerToken())
      )

      service.verifyUser("test-user-id").futureValue mustBe ()

      verifyNoInteractions(verificationConnector)
    }

    "handle timeout while retrieving identity" in {

      val (service, userProfileProvider, verificationConnector) =
        createService()

      implicit val request: Request[AnyContentAsEmpty.type] =
        FakeRequest("GET", "/")

      when(
        userProfileProvider.retrieveUserProfile(
          anyArg[String]
        )(anyArg[Request[_]])
      ).thenReturn(
        Future.failed(
          new TimeoutException("Identity retrieval timeout")
        )
      )

      service.verifyUser("test-user-id").futureValue mustBe()

      verifyNoInteractions(verificationConnector)
    }

    "handle upstream API failure" in {

      val (service, userProfileProvider, verificationConnector) =
        createService()

      implicit val request: Request[AnyContentAsEmpty.type] =
        FakeRequest("GET", "/")

      when(
        userProfileProvider.retrieveUserProfile(
          anyArg[String]
        )(anyArg[Request[_]])
      ).thenReturn(
        Future.failed(
          UpstreamErrorResponse(
            "Auth API failed",
            500,
            500
          )
        )
      )

      service.verifyUser("test-user-id").futureValue mustBe ()

      verifyNoInteractions(verificationConnector)
    }


    "handle unexpected identity retrieval errors" in {

      val (service, userProfileProvider, verificationConnector) =
        createService()

      implicit val request: Request[AnyContentAsEmpty.type] =
        FakeRequest("GET", "/")

      when(
        userProfileProvider.retrieveUserProfile(
          anyArg[String]
        )(anyArg[Request[_]])
      ).thenReturn(
        Future.failed(
          new RuntimeException("Invalid upstream response")
        )
      )

      service.verifyUser("test-user-id").futureValue mustBe ()

      verifyNoInteractions(verificationConnector)
    }
  }


  private def createService()
  : (WebChatVerificationService, UserProfileProvider, VerificationConnector) = {

    val userProfileProvider = mock[UserProfileProvider]
    val verificationConnector = mock[VerificationConnector]

    val service =
      new WebChatVerificationService(
        userProfileProvider,
        verificationConnector
      )

    (service, userProfileProvider, verificationConnector)
  }
}