/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.webchat.internal

import org.mockito.ArgumentMatchers.{any, eq => meq}
import org.mockito.Mockito._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Request
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.webchat.client.WebChatClient
import uk.gov.hmrc.webchat.config.WebChatConfig
import uk.gov.hmrc.webchat.repositories.CacheRepository
import uk.gov.hmrc.webchat.utils.TestCoreGet

class WebChatClientImplSpec extends AnyWordSpecLike with Matchers {
  implicit val fakeRequest: Request[_] = FakeRequest("GET", "/test")

  "Webchat client" when {
    val builder = new GuiceApplicationBuilder().configure(
      "microservice.services.digital-engagement-platform-partials.coreGetClass" -> "uk.gov.hmrc.webchat.utils.TestCoreGet",
      "microservice.services.digital-engagement-platform-partials.host" -> "localhost",
      "microservice.services.digital-engagement-platform-partials.port" -> 1111,
      "microservice.services.digital-engagement-platform-partials.protocol" -> "http"
    ).overrides(
      bind[TestCoreGet].toInstance(mock(classOf[TestCoreGet])) // for case where we test injected instance
    )

    val configuration = new WebChatConfig(builder.configuration)

    "constructing" should {
      "be able to get as injected instance" in {
        val webChat = builder.injector().instanceOf[WebChatClient]
        webChat should not be null
      }
    }

    "requesting required elements" when {
      "the request is successful" should {
        "return all elements as HTML" in {
          val cacheRepository = mock(classOf[CacheRepository])
          when {
            cacheRepository.getRequiredPartial()(any())
          } thenReturn Html("<div>Test</div>")

          val webChatClient = new WebChatClientImpl(cacheRepository, configuration)

          webChatClient.loadRequiredElements() shouldBe Some(Html("<div>Test</div>"))
          verify(cacheRepository).getRequiredPartial()(any())
        }
      }

      "there is no data returned" should {
        "return a None that will indicate the user that there is something wrong" in {
          val cacheRepository = mock(classOf[CacheRepository])
          when {
            cacheRepository.getRequiredPartial()(any())
          } thenReturn Html("")

          val webChatClient = new WebChatClientImpl(cacheRepository, configuration)

          webChatClient.loadRequiredElements() shouldBe None

          verify(cacheRepository).getRequiredPartial()(any())
        }
      }
    }

    "requesting HMRC chat skin element" should {
      "the request is successful" should {
        "return all elements as HTML" in {
          val cacheRepository = mock(classOf[CacheRepository])
          when {
            cacheRepository.getHMRCChatSkinPartial(any())(any())
          } thenReturn Html("<div>Test</div>")

          val webChatClient = new WebChatClientImpl(cacheRepository, configuration)

          webChatClient.loadHMRCChatSkinElement("popup") shouldBe Some(Html("<div>Test</div>"))
          verify(cacheRepository).getHMRCChatSkinPartial(meq("popup"))(any())
        }
      }
      "there is no data returned" should {
        "return a None that will indicate the user that there is something wrong" in {
          val cacheRepository = mock(classOf[CacheRepository])
          when {
            cacheRepository.getHMRCChatSkinPartial(any())(any())
          } thenReturn Html("")

          val webChatClient = new WebChatClientImpl(cacheRepository, configuration)

          webChatClient.loadHMRCChatSkinElement("test") shouldBe None

          verify(cacheRepository).getHMRCChatSkinPartial(meq("test"))(any())
        }
      }
    }

    "requesting chat container element" should {
      "return the html element when we specify an id" in {
        val cacheRepository = mock(classOf[CacheRepository])
        when {
          cacheRepository.getContainerPartial(any())(any())
        } thenReturn {
          Html("""<div id="test"></div>""")
        }

        val webChatClient = new WebChatClientImpl(cacheRepository, configuration)

        webChatClient.loadWebChatContainer("test") shouldBe Some(Html("""<div id="test"></div>"""))

        verify(cacheRepository).getContainerPartial(meq("test"))(any())
      }
    }
  }

  "disabled WebChat client" when {
    val builder = new GuiceApplicationBuilder().configure(
      "microservice.services.digital-engagement-platform-partials.coreGetClass" -> "uk.gov.hmrc.webchat.utils.TestCoreGet",
      "microservice.services.digital-engagement-platform-partials.host" -> "localhost",
      "microservice.services.digital-engagement-platform-partials.port" -> 1111,
      "microservice.services.digital-engagement-platform-partials.protocol" -> "http",
      "dep-webchat.enabled" -> false
    ).overrides(
      bind[TestCoreGet].toInstance(mock(classOf[TestCoreGet])) // for case where we test injected instance
    )
    val configuration = new WebChatConfig(builder.configuration)
    "requesting required elements" should {
      "return empty" in {
        val cacheRepository = mock(classOf[CacheRepository])
        when {
          cacheRepository.getRequiredPartial()(any())
        } thenReturn Html("<div>Test</div>")

        val webChatClient = new WebChatClientImpl(cacheRepository, configuration)
        webChatClient.loadRequiredElements() shouldBe None
      }
    }

    "requesting hmrc chat skin element" should {
      "return empty" in {
        val cacheRepository = mock(classOf[CacheRepository])
        when {
          cacheRepository.getHMRCChatSkinPartial(any())(any())
        } thenReturn Html("<div>Test</div>")

        val webChatClient = new WebChatClientImpl(cacheRepository, configuration)
        webChatClient.loadHMRCChatSkinElement("test") shouldBe None
      }
    }

    "requesting chat container elements" should {
      "return empty" in {
        val cacheRepository = mock(classOf[CacheRepository])
        when {
          cacheRepository.getContainerPartial(meq("ID"))(any())
        } thenReturn Html("<div>Test</div>")

        val webChatClient = new WebChatClientImpl(cacheRepository, configuration)
        webChatClient.loadWebChatContainer("ID") shouldBe None
      }
    }
  }
}
