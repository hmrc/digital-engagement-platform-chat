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

import uk.gov.hmrc.playcrosscompilation.AbstractPlayCrossCompilation
import uk.gov.hmrc.playcrosscompilation.PlayVersion._

object PlayCrossCompilation extends AbstractPlayCrossCompilation(defaultPlayVersion = Play28) {
  def version: String = playVersion match {
    case Play28 => "2.8.7"
    case _ => throw new RuntimeException(
      s"PlayCrossCompilation.version: Unsupported play version: $playVersion. You need to add a case for this version in order to build."
    )
  }

  def scalaVersion: String = {
    playVersion match {
      case Play28 => "2.12.9"
      case _ => throw new RuntimeException(
        s"PlayCrossCompilation.scalaVersion: Unsupported play version: $playVersion. You need to add a case for this version in order to build."
      )
    }
  }

  def crossScalaVersions: Seq[String] = {
    playVersion match {
      case Play28 => Seq("2.12.9")
      case _ => throw new RuntimeException(
        s"PlayCrossCompilation.crossScalaVersions: Unsupported play version: $playVersion. You need to add a case for this version in order to build."
      )
    }
  }
}
