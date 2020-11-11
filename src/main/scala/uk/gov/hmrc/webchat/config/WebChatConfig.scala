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

package uk.gov.hmrc.webchat.config

import com.typesafe.config.Config
import javax.inject.Inject
import play.api.Configuration

class WebChatConfig @Inject()(configuration: Configuration) {
  private val defaultRefreshSeconds = 60
  private val defaultExpireSeconds = 3600
  private val path = "microservice.services.digital-engagement-platform-partials"

  lazy val serviceUrl : String = configuration.get[Service](path) + "/engagement-platform-partials/"
  lazy val refreshSeconds : Int = configuration.getOptional[Int](s"$path.refreshAfter").getOrElse(defaultRefreshSeconds)
  lazy val expireSeconds : Int = configuration.getOptional[Int](s"$path.expireAfter").getOrElse(defaultExpireSeconds)
  lazy val underlying: Config = configuration.underlying
}
