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

package uk.gov.hmrc.webchat.config

import javax.inject.Inject
import play.api.Configuration

class WebChatConfig @Inject()(configuration: Configuration) {
  private val defaultRefreshSeconds = 60
  private val defaultExpireSeconds = 3600
  private val defaultRetrievalTimeoutSeconds = 20
  private val defaultMaxCacheEntries = 1000
  private val defaultCoreGetClass = "uk.gov.hmrc.play.bootstrap.http.HttpClient"
  private val defaultContainerIds = Seq("HMRC_Fixed_1", "HMRC_Anchored_1")

  private val path = "microservice.services.digital-engagement-platform-partials"

  lazy val partialsBaseUrl : String = configuration.get[Service](path) + "/engagement-platform-partials"
  lazy val refreshSeconds : Int = configuration.getOptional[Int](s"$path.cache.refreshAfter").getOrElse(defaultRefreshSeconds)
  lazy val expireSeconds : Int = configuration.getOptional[Int](s"$path.cache.expireAfter").getOrElse(defaultExpireSeconds)
  lazy val retrievalTimeout : Int = configuration.getOptional[Int](s"$path.cache.retrievalTimeout").getOrElse(defaultRetrievalTimeoutSeconds)
  lazy val maxCacheEntries : Int = configuration.getOptional[Int](s"$path.cache.maxEntries").getOrElse(defaultMaxCacheEntries)
  lazy val coreGetClass: String = configuration.getOptional[String](s"$path.coreGetClass").getOrElse(defaultCoreGetClass)
  lazy val containerIds: Seq[String] = configuration.getOptional[Seq[String]]("dep-webchat.container-ids").getOrElse(defaultContainerIds)
  lazy val enabled: Boolean = configuration.getOptional[Boolean]("dep-webchat.enabled").getOrElse(true)

  lazy val preProdMode: Boolean = configuration.getOptional[Boolean]("pre-prod.mode").getOrElse(true)
  lazy val depSkinBaseUrl: String = configuration.get[String]("urls.digital-engagement-platform-skin")

  lazy val nuanceUrl: String = if (preProdMode) {
    configuration.get[String]("urls.pre-production.nuance")
  } else {
    configuration.get[String]("urls.production.nuance")
  }

  lazy val hmrcSkinJSUrl: String = s"$depSkinBaseUrl/engagement-platform-skin/assets/javascripts/hmrcChatSkinBundle.js"
  lazy val hmrcSkinEmbeddedCSSUrl: String = s"$depSkinBaseUrl/engagement-platform-skin/assets/stylesheets/chat-ui-embedded.css"
  lazy val hmrcSkinPopupCSSUrl: String = s"$depSkinBaseUrl/engagement-platform-skin/assets/stylesheets/chat-ui-popup.css"
}
