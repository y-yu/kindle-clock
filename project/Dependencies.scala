import sbt._

import Keys._

object Dependencies {
  val scala213 = "2.13.7"
  val scala3 = "3.1.0"

  val isScala3 = Def.setting(
    CrossVersion.partialVersion(scalaVersion.value).exists(_._1 == 3)
  )

  lazy val domain = Def.setting {
    Seq(
      "com.google.inject" % "guice" % "5.0.1",
      "org.scala-lang.modules" %% "scala-xml" % "2.0.1",
      "org.scalatest" %% "scalatest" % "3.2.10" % "test",
      "org.mockito" % "mockito-core" % "4.1.0" % "test"
    ) ++ (if (scalaBinaryVersion.value == "3") {
            Seq(
              "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test cross CrossVersion.for3Use2_13 exclude ("org.scalatest", "scalatest_2.13"),
              "com.typesafe.play" %% "play-json" % "2.10.0-RC5"
            )
          } else {
            Seq(
              "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
              "com.typesafe.play" %% "play-json" % "2.9.2"
            )
          })
  }

  lazy val useCase = Nil

  lazy val infra = Seq(
    "redis.clients" % "jedis" % "3.8.0",
    "com.squareup.okhttp3" % "okhttp" % "4.9.3",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.1" cross CrossVersion.for3Use2_13
  )

  lazy val primary = Seq(
    "org.apache.xmlgraphics" % "batik-transcoder" % "1.14",
    "org.apache.xmlgraphics" % "batik-codec" % "1.14"
  )
}
