name := "enwie"
 
version := "1.0" 
      
lazy val `enwie` = (project in file(".")).enablePlugins(PlayJava)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "com.wordnik" %% "swagger-play2" % "1.3.12" exclude("org.reflections", "reflections"),
  "org.webjars" % "swagger-ui" % "2.1.8-M1",
  "org.reflections" % "reflections" % "0.9.8" notTransitive (),
  "commons-io" % "commons-io" % "2.4",
  "org.apache.poi" % "poi" % "3.15","org.apache.poi" % "poi-ooxml" % "3.15",
  "com.itextpdf" % "itextpdf" % "5.5.10",
  "com.itextpdf.tool" % "xmlworker" % "5.5.10",
  "org.jsoup" % "jsoup" % "1.7.2"  

)

//unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

libraryDependencies += filters

PlayKeys.playDefaultPort := 3101