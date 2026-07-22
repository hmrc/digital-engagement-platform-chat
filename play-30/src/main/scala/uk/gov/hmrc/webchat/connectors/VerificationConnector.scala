/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.webchat.connectors

import play.api.libs.json.Json
import play.api.libs.ws.writeableOf_JsValue
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.webchat.config.WebChatConfig
import uk.gov.hmrc.webchat.models.verificationservice.UserVerificationRequest

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VerificationConnector @Inject() (httpClientV2: HttpClientV2, appConfig: WebChatConfig)(implicit val ec: ExecutionContext) {

  def sendVerificationDetails(request: UserVerificationRequest)(implicit hc: HeaderCarrier): Future[HttpResponse] = {

    val url = url"${appConfig.sendVerificationDetailsUrl}"
    val requestBody = Json.toJson(request)

    httpClientV2
      .post(url)
      .withBody(requestBody)
      .execute[HttpResponse]
  }
}