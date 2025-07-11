import sbt._

object AppDependencies {

  val play30 = Seq(
    "uk.gov.hmrc" %% "play-partials-play-30"    % "10.1.0",
    "uk.gov.hmrc" %% "bootstrap-common-play-30" % "9.14.0"
  )

  val play30Test = Seq(
    "uk.gov.hmrc"           %% "bootstrap-test-play-30"   % "9.14.0"   % "test"
  )

  lazy val test: Seq[ModuleID] = Seq(
    "org.scalatest"         %% "scalatest"                % "3.2.12"  % "test",
    "org.mockito"           %  "mockito-core"             % "4.5.1"   % "test",
    "com.vladsch.flexmark"  % "flexmark-all"              % "0.62.2"  % "test",
  )
}
