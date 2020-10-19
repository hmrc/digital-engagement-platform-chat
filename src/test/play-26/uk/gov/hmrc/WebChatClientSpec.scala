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

package uk.gov.hmrc.client

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.Matchers._
import org.scalatest.WordSpecLike
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.Configuration
import uk.gov.hmrc.config.ApplicationConfig
import uk.gov.hmrc.http.{HeaderCarrier, HttpException, HttpGet, HttpResponse}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


class WebChatClientSpec extends WordSpecLike {
  "Webchat client" when {
    "requesting webchat elements" should {
      "return all elements" in {
          when {
            httpGet.GET[HttpResponse](any())(any(),any(),any())
          } thenReturn {
            Future.successful(HttpResponse(200,"<div>Test</div>"))
          }

          val webChatClient = new WebChatClient(configuration: Configuration)

          val result = Await.result(webChatClient.getElements());

          result shouldBe "<div>Test</div>"
      }
    }
  }

  implicit val hc: HeaderCarrier = new HeaderCarrier
  val httpGet = mock[HttpGet]
  val config = mock[ApplicationConfig]
  val webChatConnector = new WebChatConnector(httpGet,config)
}
