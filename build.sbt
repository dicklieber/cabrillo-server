name := "wfdserver"

organization := "com.wa9nnn"

maintainer := "wa9nnn@u505.com"


lazy val `wfdserver` = (project in file(".")).enablePlugins(PlayScala, BuildInfoPlugin, LinuxPlugin, UniversalPlugin).settings(
  buildInfoKeys ++= Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion,
    git.gitCurrentTags, git.gitCurrentBranch, git.gitHeadCommit, git.gitHeadCommitDate, git.baseVersion,
    BuildInfoKey.action("buildTime") {
      System.currentTimeMillis
    } // re-computed each time at compile)
  ),
  //    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, git.gitCurrentTags, git.gitCurrentBranch),
  buildInfoPackage := "com.wa9nnn.wfdserver"
)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += Resolver.bintrayRepo("dicklieber", "maven")

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
  "com.wa9nnn" %% "cabrillo-lib" % "1.0.0",
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
  "org.planet42" %% "laika-core" % "0.12.1",
  "com.opencsv" % "opencsv" % "5.1"
)

//RPM
packageSummary in Linux := "wfdserver"

rpmVendor := "wa9nnn"

packageDescription := """Checks and ingests WFD cabrillo files into a database & scores"""


rpmLicense := Some("None")

rpmRelease := {
  System.currentTimeMillis.toString
}


import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._


releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,              // : ReleaseStep
  inquireVersions,                        // : ReleaseStep
  runClean,                               // : ReleaseStep
  runTest,                                // : ReleaseStep
  setReleaseVersion,                      // : ReleaseStep
  commitReleaseVersion,                   // : ReleaseStep, performs the initial git checks
  tagRelease,                             // : ReleaseStep
  releaseStepCommand("universal:packageBin"),
//  publishArtifacts,                       // : ReleaseStep, checks whether `publishTo` is properly set up
  setNextVersion,                         // : ReleaseStep
  commitNextVersion,                      // : ReleaseStep
  pushChanges                             // : ReleaseStep, also checks that an upstream branch is properly configured
)

ThisBuild / organizationName := "Dick Lieber WA9NNN"
ThisBuild / organizationHomepage := Some(url("http://www.u505.com/cabrillo"))
ThisBuild / licenses := List("GPL-3.0" -> new URL("https://www.gnu.org/licenses/quick-guide-gplv3.html"))

publishTo := Some(Resolver.bintrayRepo("dicklieber", "WinterFielddayServer"))
javaOptions in Universal ++= Seq(
  // JVM memory tuning
//  "-J-Xmx1024m",
//  "-J-Xms512m",

  // Since play uses separate pidfile we have to provide it with a proper path
  // name of the pid file must be play.pid
  s"-Dpidfile.path=../wfdvar/run/${packageName.value}/play.pid",

  "-Dhttp.port=80",

)

routesImport += "com.wa9nnn.wfdserver.model.CallSign.callSignPathBindable,com.wa9nnn.wfdserver.model.CallSign"