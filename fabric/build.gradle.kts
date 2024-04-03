plugins {
  id("quiet-fabric-loom")
  id("common-conventions")
  id("platform-conventions")
  id("blossom-stuff")
  id("org.jetbrains.gradle.plugin.idea-ext")
}

val shade: Configuration by configurations.creating
val minecraftVersion = libs.versions.minecraft.get()

val journeymap_api_version = "1.20.4-1.9-fabric-SNAPSHOT"
dependencies {
  minecraft(libs.minecraft)
  mappings(loom.layered {
    officialMojangMappings()
    parchment("org.parchmentmc.data:parchment-1.20.4:2024.02.25@zip")
  })
  modImplementation(libs.fabricLoader)
  modImplementation(libs.fabricApi)

  modImplementation(libs.cloudFabric)
  include(libs.cloudFabric)
  implementation(libs.cloudMinecraftExtras)
  include(libs.cloudMinecraftExtras)

  include(libs.adventurePlatformFabric)
  modImplementation(libs.adventurePlatformFabric)

  modCompileOnlyApi("info.journeymap", "journeymap-api", journeymap_api_version)
  modLocalRuntime("info.journeymap", "journeymap-api", journeymap_api_version)
  modRuntimeOnly("curse.maven:journeymap-32274:5211236")
  modLocalRuntime("curse.maven:journeymap-32274:5211236")
  modLocalRuntime(libs.fabricApi)
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
      "mod_name" to rootProject.nameString(true),
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


sourceSets {
  main {
    blossom {
      javaSources {
        property("modid", project.name)
        property("shortVersion", providers.gradleProperty("version").get())
        property("version", project.versionString())
        property("gitBranch", project.currentBranch())
        property("gitTag", project.currentTag())
        property("gitCommit", project.lastCommitHash(false))
        property("gitUrl", "https://github.com/${providers.gradleProperty("githubOrg").get()}/${providers.gradleProperty("githubRepo").get()}")
        property("minecraftVersion", libs.versions.minecraft.get())
        property("fabricLoaderVersion", libs.versions.fabricLoader.get())
        property("fabricApiVersion", libs.versions.fabricApi.get())
      }
    }
  }
}
