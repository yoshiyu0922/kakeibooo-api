name := """kakeibooo-api"""
organization := "com.kakeibooo.api"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

libraryDependencies += guice
libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.2" % Test,
  "mysql" % "mysql-connector-java" % "5.1.16",
  "org.scalikejdbc" %% "scalikejdbc" % "3.2.+",
  "org.scalikejdbc" %% "scalikejdbc-config" % "3.2.+",
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.6.0-scalikejdbc-3.2",
  "com.pauldijou" %% "jwt-play-json" % "2.1.0",
  "org.sangria-graphql" %% "sangria-play-json" % "2.0.1",
  "org.sangria-graphql" %% "sangria-slowlog" % "2.0.0-M1",
  "org.sangria-graphql" %% "sangria-play-json" % "2.0.1",
  "commons-codec" % "commons-codec" % "1.4",
  "org.springframework.security" % "spring-security-web" % "4.2.9.RELEASE"
)
