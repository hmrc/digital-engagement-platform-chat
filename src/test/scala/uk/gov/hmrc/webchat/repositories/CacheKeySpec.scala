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

package uk.gov.hmrc.webchat.repositories


import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.SessionId

class CacheKeySpec extends AnyWordSpecLike with Matchers{
  "CacheKey" should {
    "print out relevant information" when {
      "all parameters are defined" in {
        val key = CacheKey(HeaderCarrier(sessionId = Some(SessionId("session1")), deviceID = Some("devid1")))
        key.toString shouldEqual "CacheKey(sessionId: session1, deviceId: devid1)"
      }
      "minimal parameters are defined" in {
        val key = CacheKey(HeaderCarrier())
        key.toString shouldEqual "CacheKey(sessionId: None, deviceId: None)"
      }
    }
    "support equality comparison" when {
      "sessions and device id are equal" in {
        val key1 = CacheKey(HeaderCarrier(sessionId = Some(SessionId("session1")), deviceID = Some("devid1")))
        val key1Equal = CacheKey(HeaderCarrier(sessionId = Some(SessionId("session1")), deviceID = Some("devid1")))

        key1 shouldEqual key1Equal
        key1.hashCode() shouldEqual key1Equal.hashCode()
      }

      "only url is specified" in {
        val key1 = CacheKey(HeaderCarrier())
        val key1Equal = CacheKey(HeaderCarrier())

        key1 shouldEqual key1Equal
        key1.hashCode() shouldEqual key1Equal.hashCode()
      }

      "session is different" in {
        val keySession1 = CacheKey(HeaderCarrier(sessionId = Some(SessionId("session1"))))
        val keySession2 = CacheKey(HeaderCarrier(sessionId = Some(SessionId("session2"))))
        keySession1 should not equal keySession2
        keySession1.hashCode() should not equal keySession2.hashCode()
      }

      "device id is different (ignores difference for now)" in {
        val keyDevId1 = CacheKey(HeaderCarrier(sessionId = Some(SessionId("session1")), deviceID = Some("devid1")))
        val keyDevId2 = CacheKey(HeaderCarrier(sessionId = Some(SessionId("session1")), deviceID = Some("devid2")))
        keyDevId1 shouldEqual keyDevId2
        keyDevId1.hashCode() shouldEqual keyDevId2.hashCode()
      }
    }
  }
}
