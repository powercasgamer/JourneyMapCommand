rootProject.name = "build-logic"

pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.mizule.dev")
    maven("https://maven.fabricmc.net/")
    maven("https://repo.jpenilla.xyz/snapshots/") {
      mavenContent { snapshotsOnly() }
    }
    maven("https://repo.spongepowered.org/repository/maven-public/")
  }
}

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("../libs.versions.toml"))
    }
  }
}
