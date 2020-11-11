import sbt._

object AppDependencies {

  lazy val compile: Seq[ModuleID] = Seq(
  )

  lazy val test: Seq[ModuleID] = Seq(
    "com.typesafe.play" %% "play-test" % PlayCrossCompilation.version % "test",
    "org.pegdown"    % "pegdown"      % "1.6.0"  % "test",
    "org.scalatest" %% "scalatest"    % "3.0.8"  % "test",
    "org.mockito"    % "mockito-core"  % "3.5.13" % "test",
    "uk.gov.hmrc" %% "bootstrap-play-26" % "1.16.0" % "test"
  )
}
