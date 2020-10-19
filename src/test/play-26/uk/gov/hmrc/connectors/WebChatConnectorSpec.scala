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
import uk.gov.hmrc.hello.HelloWorld
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, HttpGet, HttpResponse}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


class WebChatConnectorSpec extends WordSpecLike {
  "When working on play 2.6" should {
    "consume build from 2.6" in {
      when {
         httpGet.GET[HttpResponse](any())(any(),any(),any())
      } thenReturn {
        Future.successful(HttpResponse(200,"<div>Test</div>"))
      }

      val result = Await.result(webChatConnector.getElements(),Duration.Inf)

      result shouldBe Right("<div>Test</div>")
    }
  }

  implicit val hc: HeaderCarrier = new HeaderCarrier
  val httpGet = mock[HttpGet]
  val config = mock[ApplicationConfig]
  val webChatConnector = new WebChatConnector(httpGet,config)
}
