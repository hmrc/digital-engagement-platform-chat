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
        play27 = compilePlay27
    ),
    resolvers += Resolver.jcenterRepo
)
  .settings(PlayCrossCompilation.playCrossCompilationSettings)

val sharedLibs = Seq(
)

val compilePlay27 = Seq(
  "uk.gov.hmrc" %% "play-partials" % "6.11.0-play-27",
  "uk.gov.hmrc" %% "bootstrap-common-play-27" % "4.1.0"
)
