import sbt._

object Dependencies {
  lazy val domain = Seq(
    "com.typesafe.play" %% "play-json" % "2.9.2",
    "com.google.inject" % "guice" % "5.0.1",
    "org.scala-lang.modules" %% "scala-xml" % "1.3.0",
    "org.scalatest" %% "scalatest" % "3.2.9" % "test",
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % "test",
    "org.mockito" % "mockito-core" % "3.9.0" % "test"
  )

  lazy val useCase = Nil

  lazy val infra = Seq(
    "redis.clients" % "jedis" % "3.6.0",
    "com.squareup.okhttp3" % "okhttp" % "4.9.1"
  )

  lazy val primary = Seq(
    "org.apache.xmlgraphics" % "batik-transcoder" % "1.14",
    "org.apache.xmlgraphics" % "batik-codec" % "1.14"
  )
}
