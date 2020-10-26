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
import play.mvc.Http
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Request}
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.config.ApplicationConfig
import uk.gov.hmrc.connectors.WebChatConnector

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


class WebChatClientSpec extends WordSpecLike {
  "Webchat client" when {
    "requesting webchat elements is successful" should {
      "return all elements as HTML" in {
          when {
            webChatConnector.getElements()(any(),any())
          } thenReturn {
            Future.successful(Right("<div>Test</div>"))
          }

          val webChatClient = new WebChatClient(webChatConnector,mcc)(global)

          val result = Await.result(webChatClient.getElements(),Duration.Inf);

          result shouldBe Some(Html("<div>Test</div>"))
      }
    }

    "requesting webchat elements fails" should {
      "return None" in {
        when {
          webChatConnector.getElements()(any(),any())
        } thenReturn {
          Future.successful(Left("Request failed"))
        }

        val webChatClient = new WebChatClient(webChatConnector,mcc)(global)

        val result = Await.result(webChatClient.getElements(),Duration.Inf);

        result shouldBe None
      }
    }
  }

  implicit val global = scala.concurrent.ExecutionContext.Implicits.global
  implicit val  fakeRequest = FakeRequest("GET","/test")
  val mcc = mock[MessagesControllerComponents]
  val webChatConnector = mock[WebChatConnector]
  val config = mock[ApplicationConfig]
}
