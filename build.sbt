name := "min_bot4s_zio"

ThisBuild / organization := "yakushev"
ThisBuild / version      := "0.1.1"
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
    libraryDependencies ++= commonDependencies
  )

val Versions = new {
  val zio            = "2.0.0"
  val zhttp          = "2.0.0-RC10"
  val zioInteropCats = "22.0.0.0"
}

lazy val dependencies =
  new {
    val zio = "dev.zio" %% "zio" % Versions.zio
    val zhttp = "io.d11" %% "zhttp" % Versions.zhttp
    val ZioIoCats = "dev.zio" %% "zio-interop-cats" % Versions.zioInteropCats

    val zioDep = List(zio, zhttp, ZioIoCats)

  }

  val commonDependencies = dependencies.zioDep

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