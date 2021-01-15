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

package uk.gov.hmrc.webchat.internal

import javax.inject.Inject
import play.api.mvc.Request
import play.twirl.api.Html
import uk.gov.hmrc.webchat.client.WebChatClient
import uk.gov.hmrc.webchat.config.WebChatConfig
import uk.gov.hmrc.webchat.repositories.CacheRepository

class WebChatClientImpl @Inject()(cacheRepository: CacheRepository,
                                  webChatConfig: WebChatConfig)
  extends WebChatClient {
  def loadRequiredElements()(implicit request: Request[_]): Option[Html] = {
    getPartial (() => cacheRepository.getRequiredPartial())
  }

  def loadWebChatContainer(id: String = "HMRC_Fixed_1")(implicit request: Request[_]) : Option[Html] = {
    getPartial (() => cacheRepository.getContainerPartial(id))
  }

  private def getPartial(get: () => Html): Option[Html] = {
    if (webChatConfig.enabled) {
      val result = get()
      if (result.body.isEmpty) None else Some(result)
    } else {
      None
    }
  }
}
