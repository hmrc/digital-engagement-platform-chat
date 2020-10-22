/*
 * Copyright 2020 HM Revenue & Customs
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

package uk.gov.hmrc.connectors

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.Matchers._
import org.scalatest.WordSpecLike
import org.scalatestplus.mockito.MockitoSugar.mock
import uk.gov.hmrc.config.ApplicationConfig
import uk.gov.hmrc.http.{HeaderCarrier,HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.DefaultHttpClient
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global


class WebChatConnectorSpec extends WordSpecLike {
  "Webchat connector" should {
    "consider a 200 as a success and pass on the response body" in {
      when {
        httpClient.GET[HttpResponse](any())(any(),any(),any())
      } thenReturn {
        Future.successful(HttpResponse(200,"<div>Test</div>"))
      }

      val result = Await.result(webChatConnector.getElements(),Duration.Inf)

      result shouldBe Right("<div>Test</div>")
    }

    "consider any other response statuses as a failed request" in {
      when {
        httpClient.GET[HttpResponse](any())(any(),any(),any())
      } thenReturn {
        Future.successful(HttpResponse(400,""))
      }

      val result = Await.result(webChatConnector.getElements(),Duration.Inf)

      result shouldBe Left("Request failed")
    }
  }

  implicit val hc: HeaderCarrier = new HeaderCarrier
  val httpClient = mock[DefaultHttpClient]
  val config = mock[ApplicationConfig]
  val webChatConnector = new WebChatConnector(httpClient,config)
}
