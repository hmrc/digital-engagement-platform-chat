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

package uk.gov.hmrc.webchat.client

import uk.gov.hmrc.webchat.config.WebChatConfig
import org.mockito.Mockito._
import org.scalatest.Matchers._
import org.scalatest.WordSpecLike
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Request
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.webchat.repositories.CacheRepository
import uk.gov.hmrc.webchat.utils.SessionIdExtractor

class WebChatClientSpec extends WordSpecLike {
  "Webchat client" when {
    val builder = new GuiceApplicationBuilder().configure(
      "microservice.services.digital-engagement-platform-partials.host" -> "localhost",
      "microservice.services.digital-engagement-platform-partials.port" -> 1111,
      "microservice.services.digital-engagement-platform-partials.protocol" -> "http"
    )

    val configuration = new WebChatConfig(builder.configuration)
    val cacheRepository = mock[CacheRepository]
    val sessionIdExtractor = new SessionIdExtractor();
    implicit val  fakeRequest: Request[_] = FakeRequest("GET","/test")

    "constructing" should {
      "be able to get as injected instance" ignore {
        val webChat = builder.injector().instanceOf[WebChatClient]
        webChat should not be null
      }
    }

    "requesting webchat elements" when {
      "the request is successful" should {
        "return all elements as HTML" in {
          when {
            cacheRepository.getPartialContent("http://localhost:1111/engagement-platform-partials/none/webchat")
          } thenReturn {
            Html("<div>Test</div>")
          }

          val webChatClient = new WebChatClientImpl(cacheRepository,configuration,sessionIdExtractor)

          webChatClient.loadRequiredElements() shouldBe Some(Html("<div>Test</div>"))
        }
      }

      "there is no data returned" should {
        "return a None that will indicate the user that there is something wrong" in {
          when {
            cacheRepository.getPartialContent("http://localhost:1111/engagement-platform-partials/none/webchat")
          } thenReturn {
            Html("")
          }

          val webChatClient = new WebChatClientImpl(cacheRepository,configuration,sessionIdExtractor)

          webChatClient.loadRequiredElements() shouldBe None
        }
      }
    }

    "requesting tag div element" should {
      "return the html element when we specify an id" in {
        when {
          cacheRepository.getPartialContent("http://localhost:1111/engagement-platform-partials/tag-element/none/test")
        } thenReturn {
          Html("""<div id="test"></div>""")
        }

        val webChatClient = new WebChatClientImpl(cacheRepository,configuration,sessionIdExtractor)

        webChatClient.loadWebChatContainer("test") shouldBe Some(Html("""<div id="test"></div>"""))
      }

      "return the html element if no id is specified (default)" in {
        when {
          cacheRepository.getPartialContent("http://localhost:1111/engagement-platform-partials/tag-element/none/HMRC_Fixed_1")
        } thenReturn {
          Html("""<div id="HMRC_Fixed_1"></div>""")
        }

        val webChatClient = new WebChatClientImpl(cacheRepository,configuration,sessionIdExtractor)

        webChatClient.loadWebChatContainer() shouldBe Some(Html("""<div id="HMRC_Fixed_1"></div>"""))
      }
    }
  }
}
