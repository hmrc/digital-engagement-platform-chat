import sbt._

val appName = "digital-engagement-platform-chat"

lazy val library = Project(appName, file("."))
  .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning, SbtArtifactory)
  .settings(
    majorVersion := 0,
    crossScalaVersions := PlayCrossCompilation.crossScalaVersions,
    scalaVersion := PlayCrossCompilation.scalaVersion,
    makePublicallyAvailableOnBintray := true, // Uncomment if this is a public repository
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    libraryDependencies ++= PlayCrossCompilation.dependencies(
        shared = sharedLibs,
        play26 = compilePlay26,
        play27 = compilePlay27
    ),
    resolvers += Resolver.jcenterRepo
)
  .settings(PlayCrossCompilation.playCrossCompilationSettings)

val sharedLibs = Seq(
)

val compilePlay26 = Seq(
  "uk.gov.hmrc" %% "play-partials" % "6.11.0-play-26",
  "uk.gov.hmrc" %% "bootstrap-play-26" % "2.3.0"
)

val compilePlay27 = Seq(
  "uk.gov.hmrc" %% "play-partials" % "6.11.0-play-27",
  "uk.gov.hmrc" %% "bootstrap-common-play-27" % "3.3.0"
)
