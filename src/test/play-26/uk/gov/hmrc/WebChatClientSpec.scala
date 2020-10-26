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
import play.api.inject.guice.GuiceApplicationBuilder
import play.mvc.Http
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Request}
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.config.ApplicationConfig

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


class WebChatClientSpec extends WordSpecLike {
  "Webchat client" when {
    val builder = new GuiceApplicationBuilder().configure(
      "microservice.services.digital-engagement-platform-partials.host" -> "localhost",
      "microservice.services.digital-engagement-platform-partials.port" ->1111,
      "microservice.services.digital-engagement-platform-partials.protocol" -> "http",
      "microservice.services.digital-engagement-platform-partials.refreshAfter" -> 5,
      "microservice.services.digital-engagement-platform-partials.expireAfter" -> 5
    )
    val configuration = new ApplicationConfig(builder.configuration)

    "requesting webchat elements is successful" should {
      "return all elements as HTML" in {
          when {
            cacheRepository.getPartialContent("http://localhost:1111/engagement-platform-partials/webchat")
          } thenReturn {
            Html("<div>Test</div>")
          }

          val webChatClient = new WebChatClient(cacheRepository,configuration)

          webChatClient.getElements() shouldBe Some(Html("<div>Test</div>"))
      }
    }

    "there is no data returned" should {
      "return a None that will indicate the user that there is something wrong" in {
        when {
          cacheRepository.getPartialContent("http://localhost:1111/engagement-platform-partials/webchat")
        } thenReturn {
          Html("")
        }

        val webChatClient = new WebChatClient(cacheRepository,configuration)

        webChatClient.getElements() shouldBe None
      }
    }
  }

  implicit val global = scala.concurrent.ExecutionContext.Implicits.global
  implicit val  fakeRequest = FakeRequest("GET","/test")
  val mcc = mock[MessagesControllerComponents]
  val cacheRepository = mock[CacheRepository]
  val config = mock[ApplicationConfig]
}
