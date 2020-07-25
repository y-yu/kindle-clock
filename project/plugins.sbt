addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.7")

addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.34")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.2")

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.24")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.7.6")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.10.6"
