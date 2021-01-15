/*
 * Copyright 2021 HM Revenue & Customs
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
import javax.inject.{Inject, Singleton}
import play.api.inject.Injector
import play.api.libs.json.JsValue
import play.api.mvc.RequestHeader
import play.api.{Environment, Logger}
import play.twirl.api.Html
import uk.gov.hmrc.http.{CoreGet, HeaderCarrier}
import uk.gov.hmrc.play.HeaderCarrierConverter
import uk.gov.hmrc.webchat.config.WebChatConfig
import uk.gov.hmrc.webchat.utils.ParameterEncoder

import scala.concurrent.duration.{Duration, _}
import scala.concurrent.{Await, ExecutionContext}

@Singleton
class CacheRepository @Inject()(environment: Environment,
                                webChatConfig: WebChatConfig,
                                injector: Injector)
                               (implicit ec: ExecutionContext) {

  private val requiredKey = "REQUIRED"
  private val logger: Logger = Logger(getClass)
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

  def getRequiredPartial()(implicit request: RequestHeader): Html = {
    getPartialByKey(requiredKey)
  }

  def getContainerPartial(id: String)(implicit request: RequestHeader): Html = {
    getPartialByKey(id)
  }

  private def getPartialByKey(partialKey: String)(implicit request: RequestHeader): Html = {
    val hc = HeaderCarrierConverter.fromHeadersAndSessionAndRequest(request.headers, Some(request.session), Some(request))
    try {
      val partials = cache.get(CacheKey(hc))
      Html(partials.getOrElse(partialKey, ""))
    } catch {
      case _: Exception => Html("")
    }
  }

  private def cacheFetchPartials(key: CacheKey): Map[String, String] = {
    val encodedIds = ParameterEncoder.encodeStringList(webChatConfig.containerIds)
    val url = s"${webChatConfig.partialsBaseUrl}/partials/$encodedIds"

    logger.info(s"Fetching partial from service for $key")

    implicit val hc: HeaderCarrier = key.hc
    val result = Await.result(httpGet.GET[JsValue](url), partialRetrievalTimeout)
    result.as[Map[String, String]]
  }

  private lazy val cache: LoadingCache[CacheKey, Map[String, String]] =
    CacheBuilder.newBuilder()
      .maximumSize(maximumEntries)
      .ticker(cacheTicker)
      .refreshAfterWrite(refreshAfter.toMillis, TimeUnit.MILLISECONDS)
      .expireAfterWrite(expireAfter.toMillis, TimeUnit.MILLISECONDS)
      .build(new CacheLoader[CacheKey, Map[String, String]]() {
        def load(key: CacheKey): Map[String, String] =
          cacheFetchPartials(key)
      })

  private val cacheTicker =  Ticker.systemTicker()
}
