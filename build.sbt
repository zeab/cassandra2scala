
//Imports
import Settings._
import Dependencies._
import Docker._
import ModuleNames._
import Resolvers.allResolvers

//Add all the command alias's
CommandAlias.allCommandAlias

lazy val cassandra2scala = (project in file("."))
  .settings(rootSettings: _*)
  .settings(libraryDependencies ++= rootDependencies)
  .enablePlugins(Artifactory)
  .settings(rootDockerSettings)
  .enablePlugins(AshScriptPlugin)
  .enablePlugins(AssemblyPlugin)
  .settings(allResolvers: _*)
