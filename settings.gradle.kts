val projectName = "minesketch"

rootProject.name = projectName

include("${projectName}-core")
include("${projectName}-plugin")

pluginManagement {
  repositories {
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/")
  }
}
