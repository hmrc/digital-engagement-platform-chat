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
import play.api.Environment
import play.api.inject.Injector
import play.api.mvc.RequestHeader
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.http.{CoreGet, HeaderCarrier}
import uk.gov.hmrc.play.HeaderCarrierConverter
import uk.gov.hmrc.play.partials.HtmlPartial
import uk.gov.hmrc.webchat.config.WebChatConfig

import scala.concurrent.duration.{Duration, _}
import scala.concurrent.{Await, ExecutionContext}

import uk.gov.hmrc.play.bootstrap.http.HttpClient

class CacheRepository @Inject()(environment: Environment,
                                webChatConfig: WebChatConfig,
                                injector: Injector)
                               (implicit ec: ExecutionContext) {

  private val maximumEntries: Int = webChatConfig.maxCacheEntries
  private val refreshAfter: Duration = Duration(webChatConfig.refreshSeconds, SECONDS)
  private val expireAfter: Duration = Duration(webChatConfig.expireSeconds, SECONDS)
  private val partialRetrievalTimeout: Duration = (webChatConfig.retrievalTimeout, SECONDS)
  private val httpGet: CoreGet = {
    val bindingClass: Class[CoreGet] =
      environment.classLoader
        .loadClass(webChatConfig.coreGetClass)
        .asInstanceOf[Class[CoreGet]]
    injector.instanceOf[CoreGet](bindingClass)
  }

  def getPartialContent(url: String, errorMessage: Html = HtmlFormat.empty)(implicit request: RequestHeader): Html = {
    val hc = HeaderCarrierConverter.fromHeadersAndSessionAndRequest(request.headers, Some(request.session), Some(request))
    val key = CacheKey(url, hc)
    loadPartial(key).successfulContentOrElse(errorMessage)
  }

  private def loadPartial(key: CacheKey)(implicit request: RequestHeader): HtmlPartial =
    try {
      cache.get(key)
    } catch {
      case _: Exception => HtmlPartial.Failure()
    }

  private def cacheFetchPartial(key: CacheKey): HtmlPartial = {
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
          cacheFetchPartial(key) match {
          case s: HtmlPartial.Success => s
          case f: HtmlPartial.Failure    => throw new RuntimeException(s"Could not load partial: $f")
        }
      })

  private val cacheTicker =  Ticker.systemTicker()
}
