name := """conflix"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test,
  "com.github.t3hnar" %%	"scala-bcrypt" %	"2.4",
  "com.typesafe.play" %% "anorm" % "2.4.0",
  evolutions,
  "org.squeryl" %% "squeryl" % "0.9.6-RC3",
  "com.h2database" % "h2" % "1.4.188"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator