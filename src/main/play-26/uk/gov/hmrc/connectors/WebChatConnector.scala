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

package uk.gov.hmrc.connectors

import javax.inject.Inject
import play.api.libs.json.JsValue
import uk.gov.hmrc.config.ApplicationConfig
import uk.gov.hmrc.http.{HeaderCarrier, HttpGet, HttpResponse}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WebChatConnector @Inject()(httpGet: HttpGet, config: ApplicationConfig) {

  def getElements()(implicit hc: HeaderCarrier): Future[Either[String, String]] = {

    val response: Future[HttpResponse] = httpGet.GET[HttpResponse](config.serviceUrl)
    response.map(f =>
      f.status match {
        case 200 => {
          Right(f.body)
        }
      }
    )
  }
}
