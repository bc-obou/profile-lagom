organization in ThisBuild := "com.example"
version in ThisBuild := "1.0-SNAPSHOT"
lagomCassandraEnabled in ThisBuild := false
//lagomCassandraCleanOnStart in ThisBuild := true

lagomUnmanagedServices in ThisBuild := Map("cas_native" -> "tcp://lagomapps.cassandra.cosmos.azure.com:10350")
//lagomUnmanagedServices in ThisBuild := Map("cas_native" -> "tcp://localhost:9042")
// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.8"

javaOptions in Production += "-Dlogger.resource=logback.prod.xml"
javaOptions in Test += "-Dlogger.resource=logback.prod.xml"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test
val automapper = "io.bfil" %% "automapper" % "0.6.1"

lazy val `hello-world` = (project in file("."))
  .aggregate(`hello-world-api`, `hello-world-impl`)
//  .aggregate(`hello-world-api`, `hello-world-impl`, `hello-world-stream-api`, `hello-world-stream-impl`)

lazy val `hello-world-api` = (project in file("user-profile-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `hello-world-impl` = (project in file("user-profile-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      automapper,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings)
  .dependsOn(`hello-world-api`)

//lazy val `hello-world-stream-api` = (project in file("hello-world-stream-api"))
//  .settings(
//    libraryDependencies ++= Seq(
//      lagomScaladslApi
//    )
//  )
//
//lazy val `hello-world-stream-impl` = (project in file("hello-world-stream-impl"))
//  .enablePlugins(LagomScala)
//  .settings(
//    libraryDependencies ++= Seq(
//      lagomScaladslTestKit,
//      macwire,
//      scalaTest
//    )
//  )
//  .dependsOn(`hello-world-stream-api`, `hello-world-api`)
