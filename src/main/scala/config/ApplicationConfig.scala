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

package config

import javax.inject.Inject
import play.api.Configuration

class ApplicationConfig @Inject()(configuration: Configuration) {
  val path = "microservice.services.digital-engagement-platform-partials"
  lazy val serviceUrl : String = configuration.get[Service](path) + "/engagement-platform-partials/webchat"
  lazy val refreshSeconds : Int = configuration.getOptional[Int](s"$path.refreshAfter").getOrElse(60)
  lazy val expireSeconds : Int = configuration.getOptional[Int](s"$path.expireAfter").getOrElse(3600)
  lazy val underlying = configuration.underlying
}
