plugins {
  id("quiet-fabric-loom")
  id("common-conventions")
  id("platform-conventions")
}

val shade: Configuration by configurations.creating
val minecraftVersion = libs.versions.minecraft.get()

val journeymap_api_version = "1.20.4-1.9-fabric-SNAPSHOT"
dependencies {
  minecraft(libs.minecraft)
  mappings(loom.officialMojangMappings())
  modImplementation(libs.fabricLoader)
  modImplementation(libs.fabricApi)

  modImplementation(libs.cloudFabric)
  include(libs.cloudFabric)
  implementation(libs.cloudMinecraftExtras)
  include(libs.cloudMinecraftExtras)

  include(libs.adventurePlatformFabric)
  modImplementation(libs.adventurePlatformFabric)

  modCompileOnlyApi("info.journeymap", "journeymap-api", journeymap_api_version)
  modRuntimeOnly("curse.maven:journeymap-32274:5211236")
}

indra {
  javaVersions().target(17)
}

tasks {
  shadowJar {
    configurations = listOf(shade)
  }
  remapJar {
    archiveFileName.set("${project.nameString(true)}-mc$minecraftVersion-${project.version}.jar")
  }
  processResources {
    val replacements = mapOf(
      "mod_id" to project.name,
      "mod_name" to rootProject.name,
      "version" to version.toString(),
      "description" to project.description,
      "github" to "https://github.com/powercasgamer/JourneyMapCommand"
    )
    inputs.properties(replacements)
    filesMatching("fabric.mod.json") {
      expand(replacements)
    }
  }
}

JMCPlatform {
  productionJar.set(tasks.remapJar.flatMap { it.archiveFile })
}

publishMods.modrinth {
  modLoaders.add("fabric")
  minecraftVersions.add(minecraftVersion)
}
