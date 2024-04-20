import sbt._

import Keys._

object Dependencies {
  val scala213 = "2.13.13"
  val scala3 = "3.3.3"

  val isScala3 = Def.setting(
    CrossVersion.partialVersion(scalaVersion.value).exists(_._1 == 3)
  )

  private val effVersion = "7.0.1"
  lazy val scalaXmlDependency = "org.scala-lang.modules" %% "scala-xml" % "2.3.0"

  lazy val domain = Def.setting {
    Seq(
      "com.google.inject" % "guice" % "6.0.0",
      scalaXmlDependency,
      "org.scalatest" %% "scalatest" % "3.2.18" % "test",
      "org.mockito" % "mockito-core" % "5.11.0" % "test",
      "org.atnos" %% "eff" % effVersion,
      "org.atnos" %% "eff-monix" % effVersion,
      "org.playframework" %% "play-json" % "3.0.2",
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
    )
  }

  lazy val useCase = Nil

  lazy val infra = Seq(
    "redis.clients" % "jedis" % "5.1.2",
    "com.squareup.okhttp3" % "okhttp" % "4.12.0"
  )

  lazy val primary = Seq(
    "org.apache.xmlgraphics" % "batik-transcoder" % "1.17",
    "org.apache.xmlgraphics" % "batik-codec" % "1.17"
  )
}
