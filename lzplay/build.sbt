name := "lzplay"

version := "1.0"

lazy val `lzplay` = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "uk.co.panaxiom" %% "play-jongo" % "2.0.0-jongo1.3",
  // https://mvnrepository.com/artifact/com.rometools/rome
  "com.rometools" % "rome" % "1.7.0",
  // https://mvnrepository.com/artifact/org.apache.commons/commons-email
  "org.apache.commons" % "commons-email" % "1.4",
  javaJdbc ,  cache , javaWs
)

fork in run := true

routesGenerator := StaticRoutesGenerator