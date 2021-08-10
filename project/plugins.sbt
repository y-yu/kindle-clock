addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.8")

addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.4")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.3")

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.29")

addSbtPlugin("com.heroku" % "sbt-heroku" % "2.1.4")

addSbtPlugin("com.lightbend.sbt" % "sbt-javaagent" % "0.1.6")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.5"
