name := "wfdcheck"

version := "1.0"

lazy val `wfdcheck` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(jdbc, ehcache, ws, specs2 % Test, guice,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "org.specs2" %% "specs2-core" % "4.6.0" % "test"

)

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

