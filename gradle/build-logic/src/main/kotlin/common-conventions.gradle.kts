import com.diffplug.gradle.spotless.FormatExtension
import com.diffplug.gradle.spotless.SpotlessApply
import com.diffplug.gradle.spotless.SpotlessCheck
import com.diffplug.gradle.spotless.SpotlessExtension
import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer
import com.github.jengelman.gradle.plugins.shadow.transformers.PropertiesFileTransformer
import net.kyori.indra.licenser.spotless.HeaderFormat
import net.kyori.indra.licenser.spotless.IndraSpotlessLicenserExtension
import java.util.Calendar
import java.util.Date

plugins {
  id("base-conventions")
  id("net.kyori.indra")
  id("net.kyori.indra.publishing")
  id("net.kyori.indra.git")
  id("io.github.goooler.shadow")
  id("java-library")
}

val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

extensions.getByType(BasePluginExtension::class.java).archivesName.set(project.nameString(true))

indra {
  javaVersions {
    minimumToolchain(21)
    target(17)
  }
  mitLicense()

  publishSnapshotsTo("mizule", "https://repo.mizule.dev/snapshots")
  publishReleasesTo("mizule", "https://repo.mizule.dev/releases")
}

java {
  withSourcesJar()
  withJavadocJar()
}

val headerFile = rootProject.projectDir.resolve("HEADER")
val licenseFile = rootProject.projectDir.resolve("LICENSE")
if (headerFile.isFile && licenseFile.isFile) {
  apply(plugin = "net.kyori.indra.licenser.spotless")
  extensions.configure(SpotlessExtension::class.java) {
    fun FormatExtension.applyCommon(spaces: Int = 4) {
      trimTrailingWhitespace()
      indentWithSpaces(spaces)
      endWithNewline()
      encoding("UTF-8")
      toggleOffOn()
    }
    java {
      importOrderFile(rootProject.file("gradle/spotless/mizule.importorder"))
      removeUnusedImports()
      formatAnnotations()
      applyCommon()
    }
//        kotlinGradle {
//            applyCommon()
//            ktlint("0.50.0")
//        }
    format("configs") {
      target("**/*.yml", "**/*.yaml", "**/*.json", "**/*.conf")
      targetExclude("run/**")
      applyCommon(2)
    }
  }

  extensions.configure(IndraSpotlessLicenserExtension::class.java) {
    this.headerFormat(HeaderFormat.starSlash())
    this.licenseHeaderFile(headerFile)

    val currentYear =
      Calendar.getInstance().apply {
        time = Date()
      }.get(Calendar.YEAR)
    val createdYear = providers.gradleProperty("createdYear").map { it.toInt() }.getOrElse(currentYear)
    val year = if (createdYear == currentYear) createdYear.toString() else "$createdYear-$currentYear"

    this.property("name", providers.gradleProperty("projectName").getOrElse("template"))
    this.property("year", year)
    this.property("description", project.description ?: "A template project")
    this.property("author", providers.gradleProperty("projectAuthor").getOrElse("template"))
  }
}
tasks {
  assemble {
    dependsOn(shadowJar)
  }

  jar {
    archiveClassifier.set("unshaded")
    if (licenseFile.isFile) {
      from(licenseFile) {
        rename { "LICENSE_${providers.gradleProperty("projectName").getOrElse("template")}" }
      }
    }
  }

  shadowJar {
    archiveClassifier.set("")
    mergeServiceFiles()
    append("META-INF/spring.handlers")
    append("META-INF/spring.schemas")
    append("META-INF/spring.tooling")
    transform(PropertiesFileTransformer::class.java) {
      paths.add("META-INF/spring.factories")
      mergeStrategy = "append"
    }
    transform(Log4j2PluginsCacheFileTransformer::class.java)
  }

  clean {
    delete("run")
  }

  register("format") {
    group = "formatting"
    description = "Formats source code according to project style."
    dependsOn("spotlessApply")
  }

  withType(SpotlessCheck::class.java) {
    dependsOn(gradle.includedBuild("build-logic").task(":spotlessCheck"))
  }

  withType(SpotlessApply::class.java) {
    dependsOn(gradle.includedBuild("build-logic").task(":spotlessApply"))
  }

  withType<JavaCompile>().configureEach {
    options.isFork = true
    options.isIncremental = true
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
    options.compilerArgs.add("-Xlint:-processing")
  }

  withType<ProcessResources>().configureEach {
    filteringCharset = "UTF-8"
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    val props =
      mapOf(
        "pluginVersion" to project.versionString(),
        "pluginAuthor" to providers.gradleProperty("projectAuthor").getOrElse("template"),
        "pluginName" to providers.gradleProperty("projectName").getOrElse("template"),
        "pluginDescription" to (project.description ?: "A template project"),
      )

    filesMatching(setOf("paper-plugin.yml", "plugin.yml", "velocity-plugin.json")) {
      expand(props)
    }
  }

  javadoc {
    val options = options as? StandardJavadocDocletOptions ?: return@javadoc
    options.isAuthor = true
    options.encoding = "UTF-8"
    options.charSet = "UTF-8"
    options.linkSource()
  }
}
