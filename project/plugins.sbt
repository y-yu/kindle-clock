addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.19")

addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.6")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.0")

addSbtPlugin("com.heroku" % "sbt-heroku" % "2.1.4")

addSbtPlugin("com.github.sbt" % "sbt-git" % "2.0.1")

// addSbtPlugin("com.lightbend.sbt" % "sbt-javaagent" % "0.1.6")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.12"
