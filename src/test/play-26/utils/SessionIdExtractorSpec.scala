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

package utils

import org.scalatest.Matchers._
import org.scalatest.WordSpecLike
import play.api.test.FakeRequest
import uk.gov.hmrc.http.SessionKeys


class SessionIdExtractorSpec extends WordSpecLike {
  "Session Id extractor" when {
    "There is a session Id" should {
      "Return the session Id" in {
        val  fakeRequest = FakeRequest("GET","/test").withSession(SessionKeys.sessionId -> "test")
        val sessionIdExtractor = new SessionIdExtractor();

        val result = sessionIdExtractor.get(fakeRequest);

        result shouldBe "test"
      }
    }

    "There is no session Id" should {
      "Return `unknown`" in {
        val fakeRequest = FakeRequest("GET","/test")
        val sessionIdExtractor = new SessionIdExtractor();

        val result = sessionIdExtractor.get(fakeRequest);

        result shouldBe "none"
      }
    }
  }
}
