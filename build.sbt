import sbt.Test
import sbt._
import Keys._
//import play.sbt._
import org.scalafmt.sbt.ScalafmtPlugin.autoImport._

val scala213Version = "2.13.4"
val defaultSettings = Seq(
  scalaVersion := scala213Version,
  scalacOptions ++= Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-Xlint",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:existentials",
      "-unchecked"
    ),
  scalafmtOnCompile := true,
  addCompilerPlugin(scalafixSemanticdb)
)

val defaultDependencyConfiguration = "test->test;compile->compile"

lazy val root =
  project
    .in(file("."))
    .settings(
      name := "kindle-clock"
    )
    .aggregate(
      domain,
      usecase,
      infra,
      primary
    )

lazy val domain =
  project
    .in(file("module/domain"))
    .settings(
      libraryDependencies ++= Dependencies.domain
    )
    .settings(defaultSettings: _*)
    .disablePlugins(PlayScala, PlayLayoutPlugin, ProtocPlugin)

lazy val usecase =
  project
    .in(file("module/usecase"))
    .settings(
      libraryDependencies ++= Dependencies.useCase
    )
    .settings(defaultSettings: _*)
    .dependsOn(
      domain % defaultDependencyConfiguration
    )
    .disablePlugins(PlayScala, PlayLayoutPlugin, ProtocPlugin)

val runRedis = taskKey[Unit]("Run Redis")
lazy val infra =
  project
    .in(file("module/infra"))
    .settings(DockerUtils.runRedisSetting)
    .settings(
      // The Redis tests doesn't work if they run parallel.
      parallelExecution := false,
      Compile / PB.protoSources := Seq(
          baseDirectory.value / "src" / "proto"
        ),
      Compile / PB.targets := Seq(
          scalapb.gen(flatPackage = true) -> (sourceManaged in Compile).value
        ),
      libraryDependencies ++= Dependencies.infra,
      Test / test := (Test / test).dependsOn(runRedis).value,
      Test / testOnly := (Test / testOnly).dependsOn(runRedis).evaluated
    )
    .settings(defaultSettings: _*)
    .dependsOn(
      domain % defaultDependencyConfiguration
    )
    .disablePlugins(PlayScala, PlayLayoutPlugin)

lazy val primary = project
  .in(file("module/primary"))
  .settings(
    (Compile / unmanagedResourceDirectories) += baseDirectory.value / "conf",
    (Runtime / unmanagedClasspath) += baseDirectory.value / "conf",
    libraryDependencies += guice
  )
  .settings(defaultSettings: _*)
  .dependsOn(
    domain % defaultDependencyConfiguration,
    infra % defaultDependencyConfiguration,
    usecase % defaultDependencyConfiguration
  )
  .disablePlugins(ProtocPlugin, PlayLayoutPlugin)
  .enablePlugins(PlayScala)
