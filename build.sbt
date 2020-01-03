import NativePackagerHelper._
import play.sbt.routes.RoutesKeys

maintainer := "wa9nnn@u505.com"

name := "wfdcheck"

version := "1.3-SNAPSHOT"

organization := "org.wa9nnn"

maintainer := "wa9nnn@u505.com"


RoutesKeys.routesImport += "org.wa9nnn.wfdserver.play.Binders._"

lazy val `wfdcheck` = (project in file(".")).enablePlugins(PlayScala, BuildInfoPlugin).settings(
  buildInfoKeys ++= Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion,
    git.gitCurrentTags, git.gitCurrentBranch, git.gitHeadCommit, git.gitHeadCommitDate, git.baseVersion,
    BuildInfoKey.action("buildTime") {
      System.currentTimeMillis
    } // re-computed each time at compile)
  ),
  //    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, git.gitCurrentTags, git.gitCurrentBranch),
  buildInfoPackage := "org.wa9nnn.wfdcheck"
)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(jdbc, ehcache, ws, specs2 % Test, guice,
  "com.github.dicklieber" %% "cabrillo" % "0.3.3-SNAPSHOT",
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-json" % "2.8.1",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "org.specs2" %% "specs2-core" % "4.6.0" % "test",
  "com.typesafe.slick" %% "slick" % "3.3.2",
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.8.0",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.2",
  "com.typesafe.slick" %% "slick-codegen" % "3.3.2",
  "mysql" % "mysql-connector-java" % "8.0.18",
  "be.objectify" %% "deadbolt-scala" % "2.7.1",
  "com.github.t3hnar" %% "scala-bcrypt" % "4.1",
  "net.codingwell" %% "scala-guice" % "4.2.6",
  "commons-io" % "commons-io" % "2.6",
  "io.dropwizard.metrics" % "metrics-core" % "4.1.2",
  "nl.grons" %% "metrics4-scala" % "4.1.1",
  "org.planet42" %% "laika-core" % "0.12.1"

)


//bintrayReleaseOnPublish in ThisBuild := false

//unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

