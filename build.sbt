name := "auctions-simulation-example"

version := "1.0"

scalaVersion := "2.12.2"

resolvers ++= Seq(
  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.1",
  "com.typesafe.akka" %% "akka-remote" % "2.5.1",
  "org.economicsl" %% "esl-auctions" % "0.1.2"
)