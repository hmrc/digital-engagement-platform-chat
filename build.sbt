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
        play28 = compilePlay28
    ),
    resolvers += Resolver.jcenterRepo
)
  .settings(PlayCrossCompilation.playCrossCompilationSettings)

val sharedLibs = Seq(
)

val compilePlay28 = Seq(
  "uk.gov.hmrc" %% "play-partials" % "8.1.0-play-28",
  "uk.gov.hmrc" %% "bootstrap-common-play-28" % "4.3.0"
)
