import sbt._

val appName = "digital-engagement-platform-chat"

lazy val library = Project(appName, file("."))
  .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning, SbtArtifactory)
  .settings(
    majorVersion := 0,
    crossScalaVersions := Seq("2.11.12", "2.12.8"),
    makePublicallyAvailableOnBintray := true, // Uncomment if this is a public repository
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    libraryDependencies ++= PlayCrossCompilation.dependencies(
        shared = sharedLibs,
        play26 = compilePlay26,
        play27 = compilePlay27
    )
  )
  .settings(PlayCrossCompilation.playCrossCompilationSettings)

val sharedLibs = Seq(
  "com.typesafe.play" %% "play-test" % PlayCrossCompilation.version % "test",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
  "uk.gov.hmrc" %% "http-verbs-play-26" % "11.7.0",
  "org.mockito" % "mockito-core" % "3.5.13" % "test",
  "uk.gov.hmrc" %% "bootstrap-play-26" % "1.16.0"
)

val compilePlay26 = Seq(
  "uk.gov.hmrc" %% "play-partials" % "6.11.0-play-26"
)

val compilePlay27 = Seq(
  "uk.gov.hmrc" %% "play-partials" % "6.11.0-play-27"
)