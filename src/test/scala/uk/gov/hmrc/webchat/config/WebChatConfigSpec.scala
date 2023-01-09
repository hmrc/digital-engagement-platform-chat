/*
 * Copyright 2023 HM Revenue & Customs
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

import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers
import play.api.Configuration

class WebChatConfigSpec extends AnyWordSpecLike with Matchers{

  private def configFromFile(contents: String) = {
    val config: Config = ConfigFactory.parseString(contents)
    new WebChatConfig(Configuration(config))
  }

  "WebChatConfig" should {
    "populate local url from services" in {

      val configFile =
        """
          |microservice {
          |    services {
          |      digital-engagement-platform-partials {
          |        host = localhost
          |        port = 9109
          |      }
          |    }
          |}
          |""".stripMargin

      val webChatConfig = configFromFile(configFile)

      webChatConfig.partialsBaseUrl shouldBe "http://localhost:9109/engagement-platform-partials"
    }

    "populate protected url from services" in {

      val configFile =
        """
          |microservice {
          |    services {
          |      digital-engagement-platform-partials {
          |        protocol = https
          |        host = digital-engagement-platform-partials.protected.mdtp
          |        port = 443
          |      }
          |    }
          |}
          |""".stripMargin

      val webChatConfig = configFromFile(configFile)

      webChatConfig.partialsBaseUrl shouldBe "https://digital-engagement-platform-partials.protected.mdtp:443/engagement-platform-partials"
    }

    "default cache entries when not specified" in {

      val configFile =
        """
          |microservice {
          |    services {
          |      digital-engagement-platform-partials {
          |        host = localhost
          |        port = 9109
          |      }
          |    }
          |}
          |""".stripMargin

      val webChatConfig = configFromFile(configFile)

      webChatConfig.retrievalTimeout shouldBe 20
      webChatConfig.refreshSeconds shouldBe 60
      webChatConfig.expireSeconds shouldBe 3600
      webChatConfig.maxCacheEntries shouldBe 1000
    }

    "populate cache entries when specified" in {

      val configFile =
      """
        |microservice {
        |    services {
        |      digital-engagement-platform-partials {
        |        host = localhost
        |        port = 9109
        |        cache {
        |           refreshAfter = 4
        |           expireAfter = 42
        |           retrievalTimeout = 15
        |           maxEntries = 500
        |        }
        |        coreGetClass = "frontend.httpClient"
        |      }
        |    }
        |}
        |""".stripMargin

      val webChatConfig = configFromFile(configFile)

      webChatConfig.retrievalTimeout shouldBe 15
      webChatConfig.refreshSeconds shouldBe 4
      webChatConfig.expireSeconds shouldBe 42
      webChatConfig.maxCacheEntries shouldBe 500
    }

    "default coreGet class name" in {
      val configFile =
        """
          |microservice {
          |    services {
          |      digital-engagement-platform-partials {
          |        host = localhost
          |        port = 9109
          |      }
          |    }
          |}
          |""".stripMargin

      val webChatConfig = configFromFile(configFile)

      webChatConfig.coreGetClass shouldBe "uk.gov.hmrc.play.bootstrap.http.HttpClient"
    }

    "read coreGet class name" in {
      val configFile =
        """
          |microservice {
          |    services {
          |      digital-engagement-platform-partials {
          |        host = localhost
          |        port = 9109
          |        coreGetClass = "my.custom.coreGetClass"
          |      }
          |    }
          |}
          |""".stripMargin

      val webChatConfig = configFromFile(configFile)

      webChatConfig.coreGetClass shouldBe "my.custom.coreGetClass"
    }

    "default container ids" in {
      val configFile =
        """
          |microservice {
          |    services {
          |      digital-engagement-platform-partials {
          |        host = localhost
          |        port = 9109
          |        coreGetClass = "my.custom.coreGetClass"
          |      }
          |    }
          |}
          |""".stripMargin

      val webChatConfig = configFromFile(configFile)

      webChatConfig.containerIds shouldBe Seq("HMRC_Fixed_1", "HMRC_Anchored_1")
    }

    "read specified container ids" in {
      val configFile =
        """
          |microservice {
          |    services {
          |      digital-engagement-platform-partials {
          |        host = localhost
          |        port = 9109
          |        coreGetClass = "my.custom.coreGetClass"
          |      }
          |    }
          |}
          |dep-webchat {
          |   container-ids = [
          |     "id1",
          |     "id2"
          |   ]
          |}
          |""".stripMargin

      val webChatConfig = configFromFile(configFile)

      webChatConfig.containerIds shouldBe Seq("id1", "id2")
    }

    "default enabled to true" in {
      val configFile =
        """
          |microservice {
          |    services {
          |      digital-engagement-platform-partials {
          |        host = localhost
          |        port = 9109
          |      }
          |    }
          |}
          |""".stripMargin

      val webChatConfig = configFromFile(configFile)

      webChatConfig.enabled shouldBe true
    }

    "reads enabled state" in {
      val configFile =
        """
          |microservice {
          |    services {
          |      digital-engagement-platform-partials {
          |        host = localhost
          |        port = 9109
          |      }
          |    }
          |}
          |dep-webchat {
          |   enabled = false
          |}
          |""".stripMargin

      val webChatConfig = configFromFile(configFile)

      webChatConfig.enabled shouldBe false
    }
  }
}
