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

package uk.gov.hmrc.webchat.client


import play.api.Logging
import play.api.mvc.Request
import play.twirl.api.Html
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import uk.gov.hmrc.webchat.models.EncryptedNuanceData
import uk.gov.hmrc.webchat.services.NuanceEncryptionService
import uk.gov.hmrc.webchat.views.html.{HMRCEmbeddedView, HMRCPopupView, NuanceTagElementView, NuanceView}

import javax.inject.Inject

class WebChatClient @Inject()(nuanceEncryptionService: NuanceEncryptionService,
                              requiredElements: NuanceView,
                              popupChatSkinElement: HMRCPopupView,
                              embeddedChatSkinElement: HMRCEmbeddedView,
                              nuanceContainerElement: NuanceTagElementView) extends Logging {

  def loadRequiredElements()(implicit request: Request[_]): Option[Html] = {
    Some(withCSPNonce(requiredElements(encryptedNuanceData)))
  }
  def loadHMRCChatSkinElement(partialType: String, id: String = "")(implicit request: Request[_]): Option[Html] = {
    partialType match {
      case "popup" => Some(withCSPNonce(popupChatSkinElement(id)))
      case "embedded" => Some(withCSPNonce(embeddedChatSkinElement()))
      case partialType =>
        logger.warn(s"invalid partial type '$partialType' passed to loadHMRCChatSkinElement, defaulting to popup")
       Some(withCSPNonce(popupChatSkinElement(id)))
    }
  }
  def loadWebChatContainer(id: String = "HMRC_Fixed_1")(implicit request: Request[_]) : Option[Html] = {
    Some(withCSPNonce(nuanceContainerElement(id)))
  }

  private def encryptedNuanceData(implicit request: Request[_]) =
    EncryptedNuanceData.create(
      nuanceEncryptionService,
      HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    )

  private def withCSPNonce(fragment: Html)(implicit request: Request[_]): Html =
    Html(fragment.body.replace("{{NONCE_ATTR}}", views.html.helper.CSPNonce.attr.body))
}
