import sbt._

import Keys._

object Dependencies {
  val scala213 = "2.13.16"
  val scala3 = "3.3.6"

  val isScala3 = Def.setting(
    CrossVersion.partialVersion(scalaVersion.value).exists(_._1 == 3)
  )

  lazy val scalaXmlDependency = "org.scala-lang.modules" %% "scala-xml" % "2.4.0"

  lazy val domain = Def.setting {
    Seq(
      "com.google.inject" % "guice" % "6.0.0",
      scalaXmlDependency,
      "org.scalatest" %% "scalatest" % "3.2.19" % "test",
      "org.mockito" % "mockito-core" % "5.18.0" % "test",
      "org.playframework" %% "play-json" % "3.0.4",
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
    )
  }

  lazy val useCase = Nil

  lazy val infra = Seq(
    "redis.clients" % "jedis" % "5.1.3",
    "com.squareup.okhttp3" % "okhttp" % "5.1.0"
  )

  lazy val primary = Seq(
    "org.apache.xmlgraphics" % "batik-transcoder" % "1.19",
    "org.apache.xmlgraphics" % "batik-codec" % "1.19"
  )
}
