import sbt._

import Keys._

object Dependencies {
  val scala213 = "2.13.10"
  val scala3 = "3.3.0-RC5"

  val isScala3 = Def.setting(
    CrossVersion.partialVersion(scalaVersion.value).exists(_._1 == 3)
  )

  private val effVersion = "6.0.3"
  lazy val scalaXmlDependency = "org.scala-lang.modules" %% "scala-xml" % "2.1.0"

  lazy val domain = Def.setting {
    Seq(
      "com.google.inject" % "guice" % "7.0.0",
      scalaXmlDependency,
      "org.scalatest" %% "scalatest" % "3.2.15" % "test",
      "org.mockito" % "mockito-core" % "5.3.1" % "test",
      "org.atnos" %% "eff" % effVersion,
      "org.atnos" %% "eff-monix" % effVersion,
      "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M3" % Test,
      "com.typesafe.play" %% "play-json" % "2.10.0-RC7"
    )
  }

  lazy val useCase = Nil

  lazy val infra = Seq(
    "redis.clients" % "jedis" % "4.3.2",
    "com.squareup.okhttp3" % "okhttp" % "4.11.0"
  )

  lazy val primary = Seq(
    "org.apache.xmlgraphics" % "batik-transcoder" % "1.16",
    "org.apache.xmlgraphics" % "batik-codec" % "1.16"
  )
}
