import sbt.*
import sbt.Keys.sourceDirectories

val appName = "digital-engagement-platform-chat"

val scala2_12 = "2.12.15"
val scala2_13 = "2.13.12"
val scala3 = "3.3.5"

ThisBuild / scalaVersion       := scala2_13
ThisBuild / majorVersion       := 1
ThisBuild / isPublicArtefact   := true

lazy val library = (project in file("."))
  .settings(publish / skip := true)
  .settings(
    resolvers += Resolver.jcenterRepo
  )
  .aggregate(
    play29,
    play30
  )

lazy val play29 = Project("digital-engagement-platform-chat-29", file("play-29"))
  .enablePlugins(SbtTwirl, RoutesCompiler, BuildInfoPlugin)
  .settings(
    crossScalaVersions := Seq(scala2_13),
    libraryDependencies ++= AppDependencies.play29 ++ AppDependencies.play29Test ++ AppDependencies.test,
    Compile / TwirlKeys.compileTemplates / sourceDirectories += baseDirectory.value / s"src/main/twirl",
    TwirlKeys.constructorAnnotations += "@javax.inject.Inject()"
  )

lazy val play30 = Project("digital-engagement-platform-chat-30", file("play-30"))
  .enablePlugins(SbtTwirl, RoutesCompiler, BuildInfoPlugin)
  .settings(
    crossScalaVersions := Seq(scala2_13, scala3),
    libraryDependencies ++= AppDependencies.play30 ++ AppDependencies.play30Test ++ AppDependencies.test,
    Compile / TwirlKeys.compileTemplates / sourceDirectories += baseDirectory.value / s"src/main/twirl",
    TwirlKeys.constructorAnnotations += "@javax.inject.Inject()"
  )

val sharedLibs = Seq()
