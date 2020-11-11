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

package uk.gov.hmrc.webchat.repositories

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.SECONDS

import com.google.common.base.Ticker
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import javax.inject.Inject
import play.api.mvc.RequestHeader
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.http.{CoreGet, HeaderCarrier}
import uk.gov.hmrc.play.HeaderCarrierConverter
import uk.gov.hmrc.play.partials.HtmlPartial
import uk.gov.hmrc.webchat.config.WebChatConfig

import scala.concurrent.duration.{Duration, _}
import scala.concurrent.{Await, ExecutionContext}

class CacheRepository @Inject()(httpGet: CoreGet,
                                webChatConfig: WebChatConfig)
                               (implicit ec: ExecutionContext) {

  val maximumEntries: Int = 1000
  val refreshAfter: Duration = Duration(webChatConfig.refreshSeconds, SECONDS)
  val expireAfter: Duration = Duration(webChatConfig.expireSeconds, SECONDS)
  val partialRetrievalTimeout: Duration = (webChatConfig.retrievalTimeout, SECONDS)

  def getPartialContent(url: String, errorMessage: Html = HtmlFormat.empty)(implicit request: RequestHeader): Html = {
    val hc = HeaderCarrierConverter.fromHeadersAndSessionAndRequest(request.headers, Some(request.session), Some(request))
    val key = CacheKey(url, hc)
    loadPartial(key).successfulContentOrElse(errorMessage)
  }

  protected def loadPartial(key: CacheKey)(implicit request: RequestHeader): HtmlPartial =
    try {
      cache.get(key)
    } catch {
      case _: Exception => HtmlPartial.Failure()
    }

  private def fetchPartial(key: CacheKey): HtmlPartial = {
    implicit val hc: HeaderCarrier = key.hc
    Await.result(httpGet.GET[HtmlPartial](key.url).recover(HtmlPartial.connectionExceptionsAsHtmlPartialFailure), partialRetrievalTimeout)
  }

  private lazy val cache: LoadingCache[CacheKey, HtmlPartial.Success] =
    CacheBuilder.newBuilder()
      .maximumSize(maximumEntries)
      .ticker(cacheTicker)
      .refreshAfterWrite(refreshAfter.toMillis, TimeUnit.MILLISECONDS)
      .expireAfterWrite(expireAfter.toMillis, TimeUnit.MILLISECONDS)
      .build(new CacheLoader[CacheKey, HtmlPartial.Success]() {
        def load(key: CacheKey): HtmlPartial.Success =
          fetchPartial(key) match {
          case s: HtmlPartial.Success => s
          case f: HtmlPartial.Failure    => throw new RuntimeException("Could not load partial")
        }
      })

  private val cacheTicker =  Ticker.systemTicker()

//  override val httpGet : CoreGet = new HttpGet with WSGet {
//    override protected def actorSystem: ActorSystem = playActorSystem
//    override lazy val configuration = Some(appConfig.underlying)
//    override val hooks: Seq[HttpHook] = NoneRequired
//
//    override def wsClient: WSClient = wsclient
//  }

}