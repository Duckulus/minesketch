plugins {
  id("minesketch")
  id("io.papermc.paperweight.userdev") version "1.7.1"
  id("xyz.jpenilla.run-paper") version "1.1.0"
  id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

dependencies {
  paperweight.paperDevBundle("1.20.6-R0.1-SNAPSHOT")
  implementation(project(":${project.rootProject.name}-core"))
}

bukkit {
  main = "de.duckulus.minesketch.plugin.MinesketchPlugin"
  apiVersion = "1.20"
  commands {
    register("sketch") {
      description = "This command is used to interact with your sketches"
    }
  }
}
