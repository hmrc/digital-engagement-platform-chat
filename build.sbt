import sbt.*
import sbt.Keys.sourceDirectories

val appName = "digital-engagement-platform-chat"

val scala2_12 = "2.12.15"
val scala2_13 = "2.13.12"

ThisBuild / scalaVersion       := scala2_13
ThisBuild / majorVersion       := 1
ThisBuild / isPublicArtefact   := true

lazy val library = (project in file("."))
  .settings(publish / skip := true)
  .settings(
    resolvers += Resolver.jcenterRepo
  )
  .aggregate(
    play29
  )

lazy val play29 = Project("digital-engagement-platform-chat-29", file("play-29"))
  .enablePlugins(SbtTwirl, RoutesCompiler, BuildInfoPlugin)
  .settings(
    libraryDependencies ++= AppDependencies.play29 ++ AppDependencies.play29Test ++ AppDependencies.test,
    Compile / TwirlKeys.compileTemplates / sourceDirectories += baseDirectory.value / s"src/main/twirl",
    TwirlKeys.constructorAnnotations += "@javax.inject.Inject()"
  )

val sharedLibs = Seq()
