import sbt._

import Keys._

object Dependencies {
  val scala213 = "2.13.10"
  val scala3 = "3.2.1"

  val isScala3 = Def.setting(
    CrossVersion.partialVersion(scalaVersion.value).exists(_._1 == 3)
  )

  private val effVersion = "6.0.2"
  lazy val scalaXmlDependency = "org.scala-lang.modules" %% "scala-xml" % "2.1.0"

  lazy val domain = Def.setting {
    Seq(
      "com.google.inject" % "guice" % "5.1.0",
      scalaXmlDependency,
      "org.scalatest" %% "scalatest" % "3.2.14" % "test",
      "org.mockito" % "mockito-core" % "5.0.0" % "test",
      "org.atnos" %% "eff" % effVersion,
      "org.atnos" %% "eff-monix" % effVersion
    ) ++ (if (scalaBinaryVersion.value == "3") {
            Seq(
              "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test cross CrossVersion.for3Use2_13 exclude ("org.scalatest", "scalatest_2.13"),
              "com.typesafe.play" %% "play-json" % "2.10.0-RC7"
            )
          } else {
            Seq(
              "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
              "com.typesafe.play" %% "play-json" % "2.9.3"
            )
          })
  }

  lazy val useCase = Nil

  lazy val infra = Seq(
    "redis.clients" % "jedis" % "4.3.1",
    "com.squareup.okhttp3" % "okhttp" % "4.10.0",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.14.1" cross CrossVersion.for3Use2_13
  )

  lazy val primary = Seq(
    "org.apache.xmlgraphics" % "batik-transcoder" % "1.16",
    "org.apache.xmlgraphics" % "batik-codec" % "1.16"
  )
}
