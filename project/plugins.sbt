addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.8")

addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.3")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.2")

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.27")

addSbtPlugin("com.heroku" % "sbt-heroku" % "2.1.4")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.1"
