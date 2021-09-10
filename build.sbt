import Dependencies._
import sbt._
import Keys._
import org.scalafmt.sbt.ScalafmtPlugin.autoImport._
import play.sbt.PlayImport._

val projectName = "kindle-clock"
val herokuAppNameRemote = projectName

val defaultSettings = Seq(
  scalaVersion := scala213,
  crossScalaVersions := Seq(scala213, scala3),
  scalacOptions ++= {
    if (isScala3.value) {
      Seq(
        "-Ykind-projector",
        "-source",
        "3.0-migration"
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

val herokuSettings =
  Seq(
    Compile / herokuAppName := herokuAppNameRemote,
    Compile / herokuJdkVersion := "11",
    Compile / herokuSkipSubProjects := false
  )

val defaultDependencyConfiguration = "test->test;compile->compile"

lazy val root =
  project
    .in(file("."))
    .settings(noPublishSettings: _*)
    .settings(
      scalaVersion := scala213,
      crossScalaVersions := Seq(scala213, scala3),
      organization := "com.github.y-yu",
      name := projectName,
      description := "Kindle Clock: CFW Kindle wallpaper generating server implementation",
      homepage := Some(url("https://github.com/y-yu")),
      licenses := Seq("MIT" -> url(s"https://github.com/y-yu/$projectName/blob/master/LICENSE")),
      deployHeroku := (primary / Compile / deployHeroku).value,
      addCommandAlias("SetScala3", s"++ ${Dependencies.scala3}!")
    )
    .aggregate(
      domain,
      usecase,
      infra,
      primary
    )
    .disablePlugins(PlayScala, PlayLayoutPlugin, ProtocPlugin, HerokuPlugin)

lazy val remove213LibraryWhenScala3 =
  allDependencies := {
    if (isScala3.value) {
      // Discard dependencies for Scala 2.13 when using Scala3.
      allDependencies.value.map { x =>
        x.exclude("com.typesafe.play", "play-json_2.13")
          .exclude("org.scala-lang.modules", "scala-xml_2.13")
      }
    } else {
      allDependencies.value
    }
  }

lazy val domain =
  project
    .in(file("module/domain"))
    .settings(defaultSettings ++ noPublishSettings: _*)
    .settings(
      libraryDependencies ++= Dependencies.domain.value,
      remove213LibraryWhenScala3
    )
    .disablePlugins(PlayScala, PlayLayoutPlugin, ProtocPlugin, HerokuPlugin)

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
    .disablePlugins(PlayScala, PlayLayoutPlugin, ProtocPlugin, HerokuPlugin)

val runRedis = taskKey[Unit]("Run Redis")
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
      libraryDependencies ++= Dependencies.infra,
      Test / test := (Test / test).dependsOn(runRedis).value,
      Test / testOnly := (Test / testOnly).dependsOn(runRedis).evaluated
    )
    .dependsOn(
      domain % defaultDependencyConfiguration
    )
    .disablePlugins(PlayScala, PlayLayoutPlugin, HerokuPlugin)

lazy val primary = project
  .in(file("module/primary"))
  .settings(
    defaultSettings ++ herokuSettings: _*
  )
  .settings(
    (Compile / unmanagedResourceDirectories) += baseDirectory.value / "conf",
    (Runtime / unmanagedClasspath) += baseDirectory.value / "conf",
    libraryDependencies ++= Dependencies.primary :+ guice,
    javaAgents += "com.newrelic.agent.java" % "newrelic-agent" % "7.1.1"
  )
  .dependsOn(
    domain % defaultDependencyConfiguration,
    infra % defaultDependencyConfiguration,
    usecase % defaultDependencyConfiguration
  )
  .settings(
    libraryDependencies := libraryDependencies.value
      // Remove scala-compiler added by Heroku sbt plugin.
      .filterNot { x =>
        x.organization == "org.scala-lang" && x.name == "scala-compiler"
      }
      // Add `CrossVersion.for3Use2_13` for Play libraries added by Play sbt plugin.
      .map { x =>
        if (
          x.organization == "com.typesafe.play" &&
          x.crossVersion.isInstanceOf[CrossVersion.Binary] &&
          !x.name.contains("play-json")
        ) {
          x cross CrossVersion.for3Use2_13
        } else {
          x
        }
      },
    remove213LibraryWhenScala3,
    // Avoid version conflict scala-xml from twirl-api.
    dependencyOverrides += "org.scala-lang.modules" %% "scala-xml" % "2.0.1"
  )
  .disablePlugins(ProtocPlugin, PlayLayoutPlugin)
  .enablePlugins(PlayService, JavaAgent, JavaAppPackaging)
