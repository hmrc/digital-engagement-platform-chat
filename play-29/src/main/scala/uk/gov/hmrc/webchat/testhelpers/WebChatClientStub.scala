/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.webchat.testhelpers

import play.api.mvc.{AnyContent, Request}
import play.twirl.api.Html
import uk.gov.hmrc.webchat.client.WebChatClient
import uk.gov.hmrc.webchat.models.EncryptedNuanceData
import uk.gov.hmrc.webchat.services.NuanceEncryptionService

class WebChatClientStub extends WebChatClient {

  override def loadRequiredElements()(implicit request: Request[AnyContent]): Option[Html] = {
    Some(Html("""<div id="WEBCHAT_TEST_RequiredElements"></div>"""))
  }

  override def loadHMRCChatSkinElement(id: String)(implicit request: Request[_]): Option[Html] =
    Some(Html("""<div id="WEBCHAT_TEST_HMRCChatSkinElement"></div>"""))

  override def loadWebChatContainer(id: String)(implicit request: Request[_]): Option[Html] =
    Some(Html(s"""<div id="$id"></div>"""))

  override val nuanceEncryptionService: NuanceEncryptionService = ???
  override val nuanceView: EncryptedNuanceData => Html = ???
  override val popupChatSkinElement: Html = ???
  override val embeddedChatSkinElement: Html = ???
  override val webChatContainer: String => Html = ???
}

