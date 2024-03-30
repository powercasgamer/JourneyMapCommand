import net.kyori.indra.IndraExtension

plugins {
  id("net.kyori.indra")
  id("org.jetbrains.gradle.plugin.idea-ext")
  id("eclipse")
  id("visual-studio")
}

val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

group = rootProject.group
version = rootProject.version
description = rootProject.description

extensions.configure(IndraExtension::class) {
  gpl3OnlyLicense()
  github(providers.gradleProperty("githubOrg").get(), providers.gradleProperty("githubRepo").get()) {
    ci(true)
    issues(true)
    scm(true)
  }

  configurePublications {
    pom {
      developers {
        developer {
          id.set("powercas_gamer")
          name.set("Cas")
          url.set("https://mizule.dev")
          email.set("cas [at] mizule [dot] dev")
          timezone.set("Europe/Amsterdam")
        }
      }
    }
  }
}

tasks {
  named("idea") {
    notCompatibleWithConfigurationCache("https://github.com/gradle/gradle/issues/13480")
  }
  register("cleanAll", Delete::class) {
    description = "Cleans all project files"
    dependsOn("clean", "cleanIdea", "cleanVisualStudio", "cleanEclipse")
  }
  register("setup", Copy::class) {
    from(rootProject.rootDir.resolve("gradle/scripts"), "pre-commit")
    into(rootProject.rootDir.resolve(".git/hooks"))
    fileMode = 775
    description = "Setup the project files (hooks, etc.)"
  }
}

idea {
  module {
    isDownloadJavadoc = true
    isDownloadSources = true
  }
}
