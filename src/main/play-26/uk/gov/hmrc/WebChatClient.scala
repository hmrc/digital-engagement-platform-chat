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

package uk.gov.hmrc.client

import javax.inject.Inject
import play.api.mvc.Request
import play.twirl.api.Html
import uk.gov.hmrc.connectors.WebChatConnector
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}


class WebChatClient @Inject()(webChatConnector: WebChatConnector) {
  def getElements()(implicit request: Request[_], hc: HeaderCarrier, ec: ExecutionContext): Future[Option[Html]] = {
    webChatConnector.getElements().map { response =>
      response match {
        case Right(t) => Some(Html(t))
        case _ => None
      }
    }
  }
}
