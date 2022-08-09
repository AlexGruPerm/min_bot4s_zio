name := "min_bot4s_zio"

ThisBuild / organization := "yakushev"
ThisBuild / version      := "0.1.3"
ThisBuild / scalaVersion := "2.12.15"

lazy val global = project
  .in(file("."))
  .settings(commonSettings)
  .disablePlugins(AssemblyPlugin)
  .aggregate(
    tbot
  )

lazy val tbot = (project in file("tbot"))
  .settings(
    assembly / assemblyJarName := "tbot.jar",
    name := "tbot",
    commonSettings,
    libraryDependencies ++= commonDependencies,
    mainClass / run := "app.Main"
  )

val Versions = new {
  val zio            = "2.0.0"
  val zioTsConf      = "3.0.1"
  val zhttp          = "2.0.0-RC10"
  val zioInteropCats = "22.0.0.0"
  val sttp           = "3.7.2"
  val bot4s          = "5.6.0"
}

lazy val dependencies =
  new {
    val zio = "dev.zio" %% "zio" % Versions.zio
    val zhttp = "io.d11" %% "zhttp" % Versions.zhttp
    val ZioIoCats = "dev.zio" %% "zio-interop-cats" % Versions.zioInteropCats
    val zio_config_typesafe = "dev.zio" %% "zio-config-typesafe" % Versions.zioTsConf

    val zioDep = List(zio, zhttp, ZioIoCats, zio_config_typesafe)

    val zio_sttp = "com.softwaremill.sttp.client3" %% "zio" % Versions.sttp
    val sttp_client_backend_zio = "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % Versions.sttp

    val sttpDep = List(zio_sttp, sttp_client_backend_zio)

    val bot4s_core = "com.bot4s" %% "telegram-core" %  Versions.bot4s
    val bot4s_akka = "com.bot4s" %% "telegram-akka" %  Versions.bot4s

    val bot4slibs = List(bot4s_core,bot4s_akka)

  }

  val commonDependencies =
    dependencies.zioDep ++
    dependencies.bot4slibs ++
      dependencies.sttpDep

  lazy val compilerOptions = Seq(
          "-deprecation",
          "-encoding", "utf-8",
          "-explaintypes",
          "-feature",
          "-unchecked",
          "-language:postfixOps",
          "-language:higherKinds",
          "-language:implicitConversions",
          "-Xcheckinit",
          "-Xfatal-warnings",
          "-Ywarn-unused:params,-implicits"
  )

  lazy val commonSettings = Seq(
    scalacOptions ++= compilerOptions,
    resolvers ++= Seq(
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      Resolver.sonatypeRepo("snapshots"),
      Resolver.sonatypeRepo("public"),
      Resolver.sonatypeRepo("releases"),
      Resolver.DefaultMavenRepository,
      Resolver.mavenLocal,
      Resolver.bintrayRepo("websudos", "oss-releases")
    )
  )

  addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.1" cross CrossVersion.full)

  tbot / assembly / assemblyMergeStrategy := {
    case PathList("module-info.class") => MergeStrategy.discard
    case x if x.endsWith("/module-info.class") => MergeStrategy.discard
    case PathList("META-INF", xs @ _*)         => MergeStrategy.discard
    case "reference.conf" => MergeStrategy.concat
    case _ => MergeStrategy.first
  }

