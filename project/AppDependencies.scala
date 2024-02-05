import sbt._

object AppDependencies {

  lazy val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "play-partials-play-29"            % "9.1.0",
    "uk.gov.hmrc" %% "bootstrap-common-play-29" % "8.4.0"
  )

  lazy val test: Seq[ModuleID] = Seq(
    "com.typesafe.play"     %% "play-test"                % "2.9.0"  % "test",
    "org.pegdown"           %  "pegdown"                  % "1.6.0"   % "test",
    "org.scalatest"         %% "scalatest"                % "3.2.12"  % "test",
    "org.mockito"           %  "mockito-core"             % "4.5.1"   % "test",
    "com.vladsch.flexmark"  % "flexmark-all"              % "0.62.2"  % "test",
    "uk.gov.hmrc"           %% "bootstrap-test-play-28"   % "6.3.0"   % "test"
  )
}

