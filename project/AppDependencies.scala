import sbt._

object AppDependencies {

  lazy val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "play-partials" % "8.1.0-play-28",
    "uk.gov.hmrc" %% "bootstrap-common-play-28" % "5.0.0"
  )

  lazy val test: Seq[ModuleID] = Seq(
    "com.typesafe.play" %% "play-test" % "2.8.7" % "test",
    "org.pegdown"    % "pegdown"      % "1.6.0"  % "test",
    "org.scalatest" %% "scalatest"    % "3.0.8"  % "test",
    "org.mockito"    % "mockito-core"  % "3.5.13" % "test"
  )
}
