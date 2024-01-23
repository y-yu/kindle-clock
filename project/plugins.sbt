addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.1")

addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.7")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")

addSbtPlugin("com.github.sbt" % "sbt-git" % "2.0.1")

addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.16")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.15"
