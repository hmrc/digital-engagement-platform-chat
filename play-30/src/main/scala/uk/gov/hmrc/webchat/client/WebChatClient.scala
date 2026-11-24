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
import uk.gov.hmrc.webchat.config.WebChatConfig
import uk.gov.hmrc.webchat.models.EncryptedNuanceData
import uk.gov.hmrc.webchat.models.EnvironmentConfigData
import uk.gov.hmrc.webchat.services.{NuanceEncryptionService, WebChatVerificationService}
import uk.gov.hmrc.webchat.views.html.{HMRCEmbeddedView, HMRCPopupView, NuanceTagElementView, NuanceView, NavCXoneDAView,NavCXoneWebchatView}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class WebChatClient @Inject()(nuanceEncryptionService: NuanceEncryptionService,
                              requiredElements: NuanceView,
                              cxOnerequiredDAElements: NavCXoneDAView,
                              cxOnerequiredWebchatElements: NavCXoneWebchatView,
                              popupChatSkinElement: HMRCPopupView,
                              embeddedChatSkinElement: HMRCEmbeddedView,
                              nuanceContainerElement: NuanceTagElementView,
                              webChatVerificationService: WebChatVerificationService,
                              appConfig: WebChatConfig
                             )(implicit ec: ExecutionContext) extends Logging {

  def loadRequiredElements()(implicit request: Request[_]): Option[Html] = {
    val sessionId = encryptedNuanceData.nuanceSessionId
    logger.info("INSIDE loadRequiredElements")
    if(appConfig.isIDNVenabled)
      {
        webChatVerificationService
          .verifyUser(sessionId)
          .recover {
            case ex =>
              logger.error("Webchat verification failed, continuing to load chat", ex)
          }
      }
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

  private def getEnvironmentConfigData(implicit request: Request[_])= {
    val callingUrl= request.host+request.uri
    var entryPoint=""
    logger.info("INSIDE getEnvironmentConfigData and calling url is "+ callingUrl)
    if (request.host.toLowerCase.contains("vat-online")) {
      entryPoint="aa5534f0-e94a-4400-aa07-d8611a09fa8d"
    }else if(request.host.toLowerCase.contains("wmr-ni-repayment")){
      entryPoint = "7ed2cada-2342-40bd-ac3a-5c1867399d34"
    }else if(request.host.toLowerCase.contains("self-assessment")){
      entryPoint = "f765a392-926f-4c82-8825-d0a2062923e5"
    }
    EnvironmentConfigData.create(
      appConfig.cxOneGuideBrandId,
      appConfig.cxOneGuideScriptLoaderUrl ,
      nuanceEncryptionService,
      HeaderCarrierConverter.fromRequestAndSession(request, request.session),
      /*appConfig.cxOneGuideEntryPoint*/entryPoint,
      appConfig.depCxoneAccessibilityStatementUrl
    )
  }


  def loadCXoneRequiredElements(chatType: String)(implicit request: Request[_]): Option[Html] = {
    logger.info("INSIDE loadCXoneRequiredElements "+request.host+request.uri)
    chatType match {
      case "webchat" => Some(withCSPNonce(cxOnerequiredWebchatElements(getEnvironmentConfigData)))
      case "embeddedDA" => Some(withCSPNonce(cxOnerequiredDAElements(getEnvironmentConfigData)))
      case chatType =>
        logger.warn(s"invalid partial type '$chatType' passed to loadCXoneRequiredElements, defaulting to webchat")
        Some(withCSPNonce(cxOnerequiredWebchatElements(getEnvironmentConfigData)))
    }


  }
}

