addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.16")

addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.6")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.6")

addSbtPlugin("com.heroku" % "sbt-heroku" % "2.1.4")

addSbtPlugin("com.github.sbt" % "sbt-git" % "2.0.0")

addSbtPlugin("com.lightbend.sbt" % "sbt-javaagent" % "0.1.6")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.10"
