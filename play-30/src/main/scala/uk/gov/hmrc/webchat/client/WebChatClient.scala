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
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import uk.gov.hmrc.webchat.controllers.AuthFunction
import uk.gov.hmrc.webchat.models.EncryptedNuanceData
import uk.gov.hmrc.webchat.services.NuanceEncryptionService
import uk.gov.hmrc.webchat.views.html.{HMRCEmbeddedView, HMRCPopupView, NuanceTagElementView, NuanceView}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class WebChatClient @Inject()(nuanceEncryptionService: NuanceEncryptionService,
                              requiredElements: NuanceView,
                              popupChatSkinElement: HMRCPopupView,
                              embeddedChatSkinElement: HMRCEmbeddedView,
                              nuanceContainerElement: NuanceTagElementView,
                              val authConnector: AuthConnector)(implicit ec: ExecutionContext)
  extends AuthFunction with Logging {

  def loadRequiredElements()(implicit request: Request[_]): Future[Html] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    encryptedNuanceData
      .map(requiredElements(_))
      .map(withCSPNonce)
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

  private def encryptedNuanceData(implicit hc: HeaderCarrier): Future[EncryptedNuanceData] =
    withUserProfile[EncryptedNuanceData] { profile =>
      Future.successful(
        EncryptedNuanceData.create(nuanceEncryptionService, profile)
      )
    }

  private def withCSPNonce(fragment: Html)(implicit request: Request[_]): Html =
    Html(fragment.body.replace("{{NONCE_ATTR}}", views.html.helper.CSPNonce.attr.body))
}
