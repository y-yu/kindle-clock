import sbt._

import Keys._

object Dependencies {
  val scala213 = "2.13.12"
  val scala3 = "3.3.1"

  val isScala3 = Def.setting(
    CrossVersion.partialVersion(scalaVersion.value).exists(_._1 == 3)
  )

  private val effVersion = "7.0.1"
  lazy val scalaXmlDependency = "org.scala-lang.modules" %% "scala-xml" % "2.2.0"

  lazy val domain = Def.setting {
    Seq(
      "com.google.inject" % "guice" % "5.1.0",
      scalaXmlDependency,
      "org.scalatest" %% "scalatest" % "3.2.17" % "test",
      "org.mockito" % "mockito-core" % "5.9.0" % "test",
      "org.atnos" %% "eff" % effVersion,
      "org.atnos" %% "eff-monix" % effVersion,
      "org.playframework" %% "play-json" % "3.0.2",
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
    )
  }

  lazy val useCase = Nil

  lazy val infra = Seq(
    "redis.clients" % "jedis" % "5.1.0",
    "com.squareup.okhttp3" % "okhttp" % "4.12.0"
  )

  lazy val primary = Seq(
    "org.apache.xmlgraphics" % "batik-transcoder" % "1.17",
    "org.apache.xmlgraphics" % "batik-codec" % "1.17"
  )
}
