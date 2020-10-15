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

package uk.gov.hmrc.hello

import org.scalatest.Matchers._
import org.scalatest.WordSpecLike


class HelloWorldSpecs extends WordSpecLike {

  "When working on play 2.6" should {

    "consume build from 2.6" in {
      HelloWorld.sayHello shouldBe "2.6"
    }
  }
}
