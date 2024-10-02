ThisBuild / scalaVersion := "3.5.1"
ThisBuild / organization := "it.cambi.workplanning"
ThisBuild / version := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "worker-shift-app",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.8.6",
      "com.typesafe.akka" %% "akka-http" % "10.5.3",
      "com.typesafe.akka" %% "akka-stream" % "2.8.6",
      "io.spray" %% "spray-json" % "1.3.6",
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.3",
      "com.typesafe.akka" %% "akka-testkit" % "2.8.6" % Test,
      "com.typesafe.akka" %% "akka-http-testkit" % "10.5.3" % Test,
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    )
  )

mainClass in Compile := Some("it.cambi.workplanning.WorkPlanningApp")

assembly / assemblyJarName := "work-planning.jar"

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}