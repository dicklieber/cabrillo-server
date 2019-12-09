import NativePackagerHelper._
maintainer := "wa9nnn@u505.com"

name := "wfdcheck"

version := "1.3-SNAPSHOT"

organization := "org.wa9nnn"

maintainer := "wa9nnn@u505.com"

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
  "org.wa9nnn" %% "cabrillo" % "0.2.3-SNAPSHOT",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "org.specs2" %% "specs2-core" % "4.6.0" % "test"
)


//bintrayReleaseOnPublish in ThisBuild := false

//unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

