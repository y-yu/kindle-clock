import Dependencies._
import sbt._
import Keys._
import org.scalafmt.sbt.ScalafmtPlugin.autoImport._
import play.sbt.PlayImport._

val projectName = "kindle-clock"

val defaultSettings = Seq(
  scalaVersion := scala3,
  crossScalaVersions := Seq(scala3, scala213),
  scalacOptions ++= {
    if (isScala3.value) {
      Seq(
        "-Ykind-projector"
      )
    } else {
      Seq(
        "-Xlint:infer-any",
        "-Xsource:3"
      )
    }
  },
  scalacOptions ++= Seq(
    "-feature",
    "-language:existentials",
    "-language:implicitConversions",
    "-language:higherKinds"
  ),
  scalafmtOnCompile := true
)

val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  Compile / packageDoc / publishArtifact := false,
  scalacOptions ++= Seq(
    "-unchecked",
    "-encoding",
    "UTF-8",
    "-deprecation"
  )
)

val defaultDependencyConfiguration = "test->test;compile->compile"

lazy val root =
  project
    .in(file("."))
    .settings(noPublishSettings: _*)
    .settings(
      scalaVersion := scala3,
      crossScalaVersions := Seq(scala213, scala3),
      organization := "com.github.y-yu",
      name := projectName,
      description := "Kindle Clock: CFW Kindle wallpaper generating server implementation",
      homepage := Some(url("https://github.com/y-yu")),
      licenses := Seq("MIT" -> url(s"https://github.com/y-yu/$projectName/blob/master/LICENSE")),
      addCommandAlias("SetScala3", s"++ ${Dependencies.scala3}!"),
      addCommandAlias("SetScala2", s"++ ${Dependencies.scala213}!")
    )
    .aggregate(
      domain,
      usecase,
      infra,
      primary
    )
    .disablePlugins(PlayScala, PlayLayoutPlugin, ProtocPlugin)

lazy val domain =
  project
    .in(file("module/domain"))
    .settings(defaultSettings ++ noPublishSettings: _*)
    .settings(
      libraryDependencies ++= Dependencies.domain.value,
      (Compile / sourceGenerators) += BuildInfo.createBuildInfoFileTask.taskValue
    )
    .disablePlugins(PlayScala, PlayLayoutPlugin, ProtocPlugin)

lazy val usecase =
  project
    .in(file("module/usecase"))
    .settings(defaultSettings ++ noPublishSettings: _*)
    .settings(
      libraryDependencies ++= Dependencies.useCase
    )
    .dependsOn(
      domain % defaultDependencyConfiguration
    )
    .disablePlugins(PlayScala, PlayLayoutPlugin, ProtocPlugin)

lazy val infra =
  project
    .in(file("module/infra"))
    .settings(
      defaultSettings ++ noPublishSettings ++ DockerUtils.runRedisSetting: _*
    )
    .settings(
      // The Redis tests doesn't work if they run parallel.
      parallelExecution := false,
      Compile / PB.protoSources := Seq(
        baseDirectory.value / "src" / "proto"
      ),
      Compile / PB.targets := Seq(
        scalapb.gen(flatPackage = true) -> (Compile / sourceManaged).value
      ),
      Test / PB.protoSources := Seq(
        baseDirectory.value / "src" / "test" / "proto"
      ),
      Test / PB.targets := Seq(
        scalapb.gen(flatPackage = true) -> (Test / sourceManaged).value
      ),
      libraryDependencies ++= Dependencies.infra,
      Test / test := (Test / test).dependsOn(DockerUtils.runRedis).value,
      Test / testOnly := (Test / testOnly).dependsOn(DockerUtils.runRedis).evaluated
    )
    .dependsOn(
      domain % defaultDependencyConfiguration
    )
    .disablePlugins(PlayScala, PlayLayoutPlugin)

lazy val primary = project
  .in(file("module/primary"))
  .settings(
    defaultSettings: _*
  )
  .settings(
    (Compile / unmanagedResourceDirectories) += baseDirectory.value / "conf",
    (Runtime / unmanagedClasspath) += baseDirectory.value / "conf",
    libraryDependencies ++= Dependencies.primary :+ guice
  )
  .dependsOn(
    domain % defaultDependencyConfiguration,
    infra % defaultDependencyConfiguration,
    usecase % defaultDependencyConfiguration
  )
  .disablePlugins(ProtocPlugin, PlayLayoutPlugin)
  .enablePlugins(PlayScala, JavaAppPackaging)
