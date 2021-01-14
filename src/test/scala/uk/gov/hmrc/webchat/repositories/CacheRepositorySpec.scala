/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.webchat.repositories

import org.scalatest.Matchers._
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers.{any, eq => meq}
import org.scalatest.WordSpecLike
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.webchat.utils.TestCoreGet

import scala.concurrent.Future

class CacheRepositorySpec extends WordSpecLike {

  private val mockedGet = mock[TestCoreGet]

  private val builder = new GuiceApplicationBuilder().configure(
    "microservice.services.digital-engagement-platform-partials.coreGetClass" -> "uk.gov.hmrc.webchat.utils.TestCoreGet",
    "microservice.services.digital-engagement-platform-partials.host" -> "localhost",
    "microservice.services.digital-engagement-platform-partials.port" -> 1111,
    "microservice.services.digital-engagement-platform-partials.protocol" -> "http",
    "dep-webchat.container-ids" -> Seq("DesiredID")
  ).overrides(
    bind[TestCoreGet].toInstance(mockedGet)
  )

  private val expectedUrl = "http://localhost:1111/engagement-platform-partials/partials/%5B%22DesiredID%22%5D"

  "CacheRepository" should {
    "return required elements" in {
      reset(mockedGet)
      val repository = builder.injector().instanceOf[CacheRepository]
      val partialsJson = Json.obj(
        "REQUIRED" -> "<requiredpartial>"
      )
      when(mockedGet.GET[JsValue](any())(any(), any(), any())).thenReturn(Future.successful(partialsJson))

      implicit val request: FakeRequest[Any] = FakeRequest()
      val partial = repository.getRequiredPartial()

      partial shouldBe Html("<requiredpartial>")

      verify(mockedGet, times(1)).GET[JsValue](meq(expectedUrl))(any(), any(), any())
    }

    "return None when no required elements" in {
      reset(mockedGet)
      val repository = builder.injector().instanceOf[CacheRepository]
      val partialsJson = Json.obj()
      when(mockedGet.GET[JsValue](any())(any(), any(), any())).thenReturn(Future.successful(partialsJson))

      implicit val request: FakeRequest[Any] = FakeRequest()
      val partial = repository.getRequiredPartial()

      partial shouldBe Html("")

      verify(mockedGet, times(1)).GET[JsValue](meq(expectedUrl))(any(), any(), any())
    }

    "return container elements" in {
      reset(mockedGet)
      val repository = builder.injector().instanceOf[CacheRepository]
      val partialsJson = Json.obj(
        "tag1" -> "<partial1>",
        "tag2" -> "<partial2>"
      )
      when(mockedGet.GET[JsValue](any())(any(), any(), any())).thenReturn(Future.successful(partialsJson))

      implicit val request: FakeRequest[Any] = FakeRequest()
      val partial = repository.getContainerPartial("tag1")

      partial shouldBe Html("<partial1>")

      verify(mockedGet, times(1)).GET[JsValue](meq(expectedUrl))(any(), any(), any())
    }

    "return multiple container elements" in {
      reset(mockedGet)
      val repository = builder.injector().instanceOf[CacheRepository]
      val partialsJson = Json.obj(
        "tag1" -> "<partial1>",
        "tag2" -> "<partial2>"
      )
      when(mockedGet.GET[JsValue](any())(any(), any(), any())).thenReturn(Future.successful(partialsJson))

      implicit val request: FakeRequest[Any] = FakeRequest()
      repository.getContainerPartial("tag1") shouldBe Html("<partial1>")
      repository.getContainerPartial("tag2") shouldBe Html("<partial2>")

      verify(mockedGet, times(1)).GET[JsValue](meq(expectedUrl))(any(), any(), any())
    }

    "handle exception in GET call" in {
      reset(mockedGet)
      val repository = builder.injector().instanceOf[CacheRepository]
      when(mockedGet.GET[JsValue](any())(any(), any(), any())).thenThrow(new RuntimeException("Simulated exception"))

      implicit val request: FakeRequest[Any] = FakeRequest()
      val partial = repository.getContainerPartial("tag1")

      partial shouldBe Html("")

      verify(mockedGet, times(1)).GET[JsValue](meq(expectedUrl))(any(), any(), any())
    }

  }
}
