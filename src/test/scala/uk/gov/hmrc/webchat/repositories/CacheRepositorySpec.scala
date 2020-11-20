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
    "microservice.services.digital-engagement-platform-partials.protocol" -> "http"
  ).overrides(
    bind[TestCoreGet].toInstance(mockedGet)
  )

  private val repository = builder.injector().instanceOf[CacheRepository]

  "CacheRepository" should {
    "return required elements" in {
      val partialsJson = Json.obj(
        "REQUIRED" -> "<requiredpartial>"
      )
      when(mockedGet.GET[JsValue](any())(any(), any(), any())).thenReturn(Future.successful(partialsJson))

      implicit val request: FakeRequest[Any] = FakeRequest()
      val partial = repository.getRequiredPartial()

      partial shouldBe Html("<requiredpartial>")
    }

    "return None when no required elements" in {
      val partialsJson = Json.obj(
      )
      when(mockedGet.GET[JsValue](any())(any(), any(), any())).thenReturn(Future.successful(partialsJson))

      implicit val request: FakeRequest[Any] = FakeRequest()
      val partial = repository.getRequiredPartial()

      partial shouldBe Html("")
    }

    "return container elements" in {
      val partialsJson = Json.obj(
        "tag1" -> "<partial1>",
        "tag2" -> "<partial2>"
      )
      when(mockedGet.GET[JsValue](any())(any(), any(), any())).thenReturn(Future.successful(partialsJson))

      implicit val request: FakeRequest[Any] = FakeRequest()
      val partial = repository.getContainerPartial("tag1")

      partial shouldBe Html("<partial1>")
    }
  }
}
