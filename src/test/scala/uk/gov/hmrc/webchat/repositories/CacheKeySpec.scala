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

import org.scalatest.WordSpecLike
import org.scalatest.Matchers._

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.logging.SessionId

class CacheKeySpec extends WordSpecLike {
  "CacheKey" should {
    "print out relevant information" when {
      "all parameters are defined" in {
        val key = CacheKey("url1", HeaderCarrier(sessionId = Some(SessionId("session1")), deviceID = Some("devid1")))
        key.toString shouldEqual "CacheKey(url: url1, sessionId: session1, deviceId: devid1)"
      }
      "minimal parameters are defined" in {
        val key = CacheKey("url1", HeaderCarrier())
        key.toString shouldEqual "CacheKey(url: url1, sessionId: None, deviceId: None)"
      }
    }
    "support equality comparison" when {
      "url and session and device id are equal" in {
        val key1 = CacheKey("url1", HeaderCarrier(sessionId = Some(SessionId("session1")), deviceID = Some("devid1")))
        val key1Equal = CacheKey("url1", HeaderCarrier(sessionId = Some(SessionId("session1")), deviceID = Some("devid1")))

        key1 shouldEqual key1Equal
        key1.hashCode() shouldEqual key1Equal.hashCode()
      }

      "only url is specified" in {
        val key1 = CacheKey("url1", HeaderCarrier())
        val key1Equal = CacheKey("url1", HeaderCarrier())

        key1 shouldEqual key1Equal
        key1.hashCode() shouldEqual key1Equal.hashCode()
      }

      "session is different" in {
        val keySession1 = CacheKey("url1", HeaderCarrier(sessionId = Some(SessionId("session1"))))
        val keySession2 = CacheKey("url1", HeaderCarrier(sessionId = Some(SessionId("session2"))))
        keySession1 should not equal keySession2
        keySession1.hashCode() should not equal keySession2.hashCode()
      }

      "url is different" in {
        val keyUrl1 = CacheKey("url1", HeaderCarrier(sessionId = Some(SessionId("session1"))))
        val keyUrl2 = CacheKey("url2", HeaderCarrier(sessionId = Some(SessionId("session1"))))
        keyUrl1 should not equal keyUrl2
        keyUrl1.hashCode() should not equal keyUrl2.hashCode()
      }

      "device id is different (ignores difference for now)" in {
        val keyDevId1 = CacheKey("url1", HeaderCarrier(sessionId = Some(SessionId("session1")), deviceID = Some("devid1")))
        val keyDevId2 = CacheKey("url1", HeaderCarrier(sessionId = Some(SessionId("session1")), deviceID = Some("devid2")))
        keyDevId1 shouldEqual keyDevId2
        keyDevId1.hashCode() shouldEqual keyDevId2.hashCode()
      }
    }
  }
}
