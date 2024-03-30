import me.modmuss50.mpp.ReleaseType
import org.gradle.kotlin.dsl.invoke

plugins {
  id("common-conventions")
  id("me.modmuss50.mod-publish-plugin")
}

decorateVersion()

val platformExt = extensions.create("JMCPlatform", JMCPlatformExtension::class)

tasks {
  val copyJar = register("copyJar", CopyFile::class) {
    fileToCopy.set(platformExt.productionJar)
    destination.set(platformExt.productionJar.flatMap { rootProject.layout.buildDirectory.file("libs/${it.asFile.name}") })
  }
  assemble {
    dependsOn(copyJar)
  }
}

publishMods.modrinth {
  projectId.set("crx8wIYV")
  type.set(ReleaseType.STABLE)
  file.set(platformExt.productionJar)
  changelog.set(releaseNotes)
  requires {
    id = "lfHFW1mp"
  }
  accessToken.set(providers.gradleProperty("modrinth-api-key"))
}
