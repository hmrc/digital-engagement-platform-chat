import sbt._

val appName = "digital-engagement-platform-chat"

lazy val library = Project(appName, file("."))
  .settings(
    majorVersion := 0,
    crossScalaVersions := PlayCrossCompilation.crossScalaVersions,
    scalaVersion := PlayCrossCompilation.scalaVersion,
    isPublicArtefact := true,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    libraryDependencies ++= PlayCrossCompilation.dependencies(
        shared = sharedLibs
    ),
    resolvers += Resolver.jcenterRepo
  )
  .settings(PlayCrossCompilation.playCrossCompilationSettings)
  .settings(scalacSettings)

val sharedLibs = Seq()

// Scalac options
lazy val scalacSettings = Def.settings(
    // Disable fatal warnings and warnings from discarding values
    scalacOptions ~= {
        opts =>
            opts.filterNot(Set("-Xfatal-warnings", "-Ywarn-value-discard"))
    },
    // Disable dead code warning as it is triggered by Mockito any()
    Test / scalacOptions ~= {
        opts =>
            opts.filterNot(Set("-Ywarn-dead-code"))
    },
    // Disable warnings arising from generated routing code
    scalacOptions += "-Wconf:src=routes/.*:silent",
    scalacOptions += "-Ypartial-unification"
)