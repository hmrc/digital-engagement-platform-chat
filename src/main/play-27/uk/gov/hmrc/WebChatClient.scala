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

import java.util.concurrent.TimeUnit.SECONDS

import akka.actor.ActorSystem
import javax.inject.Inject
import play.api.libs.ws.WSClient
import play.api.mvc.Request
import play.twirl.api.Html
import uk.gov.hmrc.config.ApplicationConfig
import uk.gov.hmrc.http.hooks.HttpHook
import uk.gov.hmrc.http.{CoreGet, HttpGet}
import uk.gov.hmrc.play.http.ws.WSGet
import uk.gov.hmrc.play.partials.CachedStaticHtmlPartialRetriever

import scala.concurrent.duration.Duration


class WebChatClient @Inject()(cacheRepository: CacheRepository, appConfig: ApplicationConfig) {
  def getElements()(implicit request: Request[_]): Option[Html] = {
    val result = cacheRepository.getPartialContent(appConfig.serviceUrl)
    if (result.body.isEmpty) None else Some(result)
  }
}

class CacheRepository @Inject()(wsclient: WSClient,
                                appConfig: ApplicationConfig,
                                playActorSystem: ActorSystem)
  extends CachedStaticHtmlPartialRetriever {

  override val httpGet : CoreGet = new HttpGet with WSGet {
    override protected def actorSystem: ActorSystem = playActorSystem
    override lazy val configuration = Some(appConfig.underlying)
    override val hooks: Seq[HttpHook] = NoneRequired

    override def wsClient: WSClient = wsclient
  }

  override def refreshAfter: Duration = Duration(appConfig.refreshSeconds, SECONDS)
  override def expireAfter: Duration = Duration(appConfig.expireSeconds, SECONDS)
}
