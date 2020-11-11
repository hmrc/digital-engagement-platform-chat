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

import javax.inject.Inject
import play.api.mvc.Request
import play.twirl.api.Html
import uk.gov.hmrc.webchat.config.WebChatConfig
import uk.gov.hmrc.webchat.repositories.CacheRepository
import uk.gov.hmrc.webchat.utils.SessionIdExtractor

class WebChatClientImpl @Inject()(cacheRepository: CacheRepository,
                                  appConfig: WebChatConfig,
                                  sessionIdExtractor: SessionIdExtractor)
  extends WebChatClient {
  def loadRequiredElements()(implicit request: Request[_]): Option[Html] = {
    val result = cacheRepository.getPartialContent(s"${appConfig.partialsBaseUrl}/${sessionIdExtractor.get(request)}/webchat")
    if (result.body.isEmpty) None else Some(result)
  }

  def loadWebChatContainer(id: String = "HMRC_Fixed_1")(implicit request: Request[_]) : Option[Html] = {
    val result = cacheRepository.getPartialContent(s"${appConfig.partialsBaseUrl}/tag-element/${sessionIdExtractor.get(request)}/$id")
    if (result.body.isEmpty) None else Some(result)
  }
}

