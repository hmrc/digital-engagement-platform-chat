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

val sharedLibs = Seq()
