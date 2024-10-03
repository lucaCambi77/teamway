ThisBuild / scalaVersion := "3.5.1"
ThisBuild / organization := "it.cambi.workplanning"
ThisBuild / version := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "worker-shift-app",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % "0.23.16",
      "org.http4s" %% "http4s-circe" % "0.23.27",
      "io.circe" %% "circe-generic" % "0.14.9",
      "io.circe" %% "circe-parser" % "0.14.9",
      "org.http4s" %% "http4s-blaze-client" % "0.23.16",
      "org.http4s" %% "http4s-dsl" % "0.23.27",
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "ch.qos.logback" % "logback-classic" % "1.5.6",
      "org.typelevel" %% "cats-effect" % "3.5.4", // Adjust the version as needed
    )
  )

mainClass in Compile := Some("it.cambi.workplanning.WorkPlanningApp")

assembly / assemblyJarName := "work-planning.jar"

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}