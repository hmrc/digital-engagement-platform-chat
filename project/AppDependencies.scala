import sbt._

object AppDependencies {

  lazy val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "play-partials"            % "8.3.0-play-28",
    "uk.gov.hmrc" %% "bootstrap-common-play-28" % "7.12.0"
  )

  lazy val test: Seq[ModuleID] = Seq(
    "com.typesafe.play"     %% "play-test"    % "2.8.18"  % "test",
    "org.pegdown"           %  "pegdown"      % "1.6.0"   % "test",
    "org.scalatest"         %% "scalatest"    % "3.2.14"  % "test",
    "org.mockito"           %  "mockito-core" % "4.5.1"   % "test",
    "com.vladsch.flexmark"  % "flexmark-all"  % "0.62.2"  % "test"
  )
}
