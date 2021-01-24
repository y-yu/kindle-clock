import sbt._
import Keys._
import org.scalafmt.sbt.ScalafmtPlugin.autoImport._

val scala213Version = "2.13.4"
val projectName = "kindle-clock"
val herokuAppNameRemote = projectName

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

val noPublishSettings = Seq(
  publish := {},
  publishLocal := {}
)

val herokuSettings =
  Seq(
    Compile / herokuAppName := herokuAppNameRemote,
    Compile / herokuJdkVersion := "11",
    herokuSkipSubProjects in Compile := false
  )

val defaultDependencyConfiguration = "test->test;compile->compile"

lazy val root =
  project
    .in(file("."))
    .settings(noPublishSettings: _*)
    .settings(
      organization := "com.github.y-yu",
      name := projectName,
      description := "Kindle Clock: CFW Kindle wallpaper generating server implementation",
      homepage := Some(url("https://github.com/y-yu")),
      licenses := Seq("MIT" -> url(s"https://github.com/y-yu/$projectName/blob/master/LICENSE")),
      deployHeroku := (primary / Compile / deployHeroku).value
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
    .settings(defaultSettings: _*)
    .settings(
      libraryDependencies ++= Dependencies.domain
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

val runRedis = taskKey[Unit]("Run Redis")
lazy val infra =
  project
    .in(file("module/infra"))
    .settings(
      defaultSettings ++ DockerUtils.runRedisSetting: _*
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
      libraryDependencies ++= Dependencies.infra,
      Test / test := (Test / test).dependsOn(runRedis).value,
      Test / testOnly := (Test / testOnly).dependsOn(runRedis).evaluated
    )
    .dependsOn(
      domain % defaultDependencyConfiguration
    )
    .disablePlugins(PlayScala, PlayLayoutPlugin)

lazy val primary = project
  .in(file("module/primary"))
  .settings(
    defaultSettings ++ herokuSettings: _*
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
  .enablePlugins(PlayScala)
