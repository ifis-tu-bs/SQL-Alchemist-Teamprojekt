name := """SQL-Alchemist-Core"""

version := "1.0"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "1.3.176",
  "org.apache.logging.log4j" % "log4j-core" % "2.2",
  "org.apache.logging.log4j" % "log4j-api" % "2.2",
  "com.typesafe" % "config" % "1.3.0-M3"
)
