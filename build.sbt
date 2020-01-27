import NativePackagerHelper._
import play.sbt.routes.RoutesKeys

maintainer := "wa9nnn@u505.com"

name := "wfdcheck"


organization := "org.wa9nnn"


lazy val `wfdcheck` = (project in file(".")).enablePlugins(PlayScala, BuildInfoPlugin, LinuxPlugin, UniversalPlugin).settings(
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

scalacOptions in(Compile, doc) ++= Seq("-verbose", "-target:jvm-1.8")

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")
initialize := {
  val _ = initialize.value
  val javaVersion = sys.props("java.specification.version")
  if (javaVersion != "1.8")
    sys.error("Java 1.8 is required for this project. Found " + javaVersion + " instead")
}


libraryDependencies ++= Seq(jdbc, ehcache, ws, specs2 % Test, guice,
  "com.github.dicklieber" %% "cabrillo" % "0.3.6",
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
  "com.github.racc" % "typesafeconfig-guice" % "0.1.0",
  "commons-io" % "commons-io" % "2.6",
  "io.dropwizard.metrics" % "metrics-core" % "4.1.2",
  "com.kenshoo" %% "metrics-play" % "2.7.3_0.8.2",
  "nl.grons" %% "metrics4-scala" % "4.1.1",
  "org.planet42" %% "laika-core" % "0.12.1"

)

//RPM
packageSummary in Linux := "wfdserver"

rpmVendor := "wa9nnn"

packageDescription := """Checks and ingests cabrillo files into a database."""


rpmLicense := Some("None")

rpmRelease := {
  System.currentTimeMillis.toString
}
publishTo := Some(Resolver.file("local-ivy", file("target/ivy-repo/releases")))


import ReleaseTransformations._
//import posterous.Publish._
//
//val publishReleaseNotes = (ref: ProjectRef) => ReleaseStep(
//  check  = releaseStepTaskAggregated(check in Posterous in ref),   // upfront check
//  action = releaseStepTaskAggregated(publish in Posterous in ref) // publish release notes
//)


releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,              // : ReleaseStep
  inquireVersions,                        // : ReleaseStep
  runClean,                               // : ReleaseStep
  runTest,                                // : ReleaseStep
  setReleaseVersion,                      // : ReleaseStep
  commitReleaseVersion,                   // : ReleaseStep, performs the initial git checks
  tagRelease,                             // : ReleaseStep
//  publishArtifacts,                       // : ReleaseStep, checks whether `publishTo` is properly set up
  releaseStepCommand("universal:packageBin"),
  setNextVersion,                         // : ReleaseStep
  commitNextVersion,                      // : ReleaseStep
  pushChanges                             // : ReleaseStep, also checks that an upstream branch is properly configured
)

javaOptions in Universal ++= Seq(
  // JVM memory tuning
//  "-J-Xmx1024m",
//  "-J-Xms512m",

  // Since play uses separate pidfile we have to provide it with a proper path
  // name of the pid file must be play.pid
  s"-Dpidfile.path=../wfdvar/run/${packageName.value}/play.pid",

  "-Dhttp.port=80",

//  // alternative, you can remove the PID file
//  // s"-Dpidfile.path=/dev/null",
//
//  // Use separate configuration file for production environment
//  s"-Dconfig.file=/usr/share/${packageName.value}/conf/production.conf",
//
//  // Use separate logger configuration file for production environment
//  s"-Dlogger.file=/usr/share/${packageName.value}/conf/production-logger.xml",
//
//  // You may also want to include this setting if you use play evolutions
//  "-DapplyEvolutions.default=true"
)