import sbt._

object AppDependencies {
  val play29 = Seq(
    "uk.gov.hmrc" %% "play-partials-play-29"    % "10.0.0",
    "uk.gov.hmrc" %% "bootstrap-common-play-29" % "9.5.0"
  )

  val play29Test = Seq(
    "com.typesafe.play"     %% "play-test"                % "2.9.0"  % "test",
    "uk.gov.hmrc"           %% "bootstrap-test-play-29"   % "8.4.0"   % "test"
  )

  val play30 = Seq(
    "uk.gov.hmrc" %% "play-partials-play-30"    % "10.0.0",
    "uk.gov.hmrc" %% "bootstrap-common-play-30" % "9.5.0"
  )

  val play30Test = Seq(
    "com.typesafe.play"     %% "play-test"                % "2.9.0"  % "test",
    "uk.gov.hmrc"           %% "bootstrap-test-play-30"   % "8.4.0"   % "test"
  )

  lazy val test: Seq[ModuleID] = Seq(
    "org.pegdown"           %  "pegdown"                  % "1.6.0"   % "test",
    "org.scalatest"         %% "scalatest"                % "3.2.12"  % "test",
    "org.mockito"           %  "mockito-core"             % "4.5.1"   % "test",
    "com.vladsch.flexmark"  % "flexmark-all"              % "0.62.2"  % "test",
  )
}
