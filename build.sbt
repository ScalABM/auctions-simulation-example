name := "auctions-simulation-example"

version := "1.0"

scalaVersion := "2.12.2"

resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.1",
  "com.typesafe.akka" %% "akka-remote" % "2.5.1",
  "com.typesafe" % "config" % "1.3.1",
  "com.typesafe.play" %% "play-json" % "2.6.0-RC2",
  "org.economicsl" %% "esl-auctions" % "0.2.0-SNAPSHOT"
)