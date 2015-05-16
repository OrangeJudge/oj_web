name := """oj_web"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, SbtWeb)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "mysql" % "mysql-connector-java" % "5.1.18",
  "org.pegdown" % "pegdown" % "1.5.0",
  "org.zeroturnaround" % "zt-zip" % "1.8",
  "com.typesafe.play" %% "play-mailer" % "2.4.0"
)

pipelineStages := Seq(uglify, digest)

LessKeys.compress in Assets := true

includeFilter in (Assets, LessKeys.less) := "*.less"

excludeFilter in (Assets, LessKeys.less) := "_*.less"

sources in (Compile, doc) ~= (_ filter (_.getName endsWith ".java"))  // Force generating JavaDoc instead of ScalaDoc

mappings in Universal ++=
  (baseDirectory.value / "scripts" * "*" get) map
    (x => x -> (x.getName))
