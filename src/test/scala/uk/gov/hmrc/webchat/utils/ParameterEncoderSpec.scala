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

package uk.gov.hmrc.webchat.utils

import org.scalatest.{Matchers, WordSpecLike}

class ParameterEncoderSpec extends WordSpecLike with Matchers {
  "ParameterEncoder" when {
    "encoding parameters" should {
      "work with an empty list" in {
        val result = ParameterEncoder.encodeStringList(Seq.empty)
        result should be("%5B%5D")
      }
      "work with a non-empty list" in {
        val result = ParameterEncoder.encodeStringList(Seq("a", "b", "c"))
        result should be("%5B%22a%22%2C%22b%22%2C%22c%22%5D")
      }
    }
    "decoding parameters" should {
      "work with an empty list" in {
        val result = ParameterEncoder.decodeStringList("%5B%5D")
        result should be(Seq.empty)
      }
      "work with a non-empty list" in {
        val result = ParameterEncoder.decodeStringList("%5B%22a%22%2C%22b%22%2C%22c%22%5D")
        result should be(Seq("a", "b", "c"))
      }
    }
  }
}
