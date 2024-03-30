import com.diffplug.gradle.spotless.FormatExtension

plugins {
  `kotlin-dsl`
  alias(libs.plugins.spotless)
  alias(libs.plugins.idea.ext)
}

repositories {
  gradlePluginPortal()
  mavenCentral()
}

dependencies {
  compileOnly(files(libs::class.java.protectionDomain.codeSource.location))
  implementation(libs.indra.common)
  implementation(libs.indra.git)
  implementation(libs.indra.spotless)
  implementation(libs.shadow)
  implementation(libs.kotlin.gradle)
  implementation(libs.kotlin.std)
  implementation(libs.idea.gradle)
  implementation(libs.blossom)
  implementation(libs.spotless)
  implementation(libs.testlogger)
  implementation(libs.build.mod.publish.plugin)
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
  target {
    compilations.configureEach {
      kotlinOptions {
        jvmTarget = "17"
      }
    }
  }
}

spotless {
  fun FormatExtension.applyCommon() {
    trimTrailingWhitespace()
    endWithNewline()
    encoding("UTF-8")
    toggleOffOn()
    target("*.gradle.kts", "src/*/kotlin/**.gradle.kts", "src/*/kotlin/**.kt")
  }
  kotlinGradle {
    applyCommon()
    ktlint(libs.versions.ktlint.get())
      .editorConfigOverride(
        mapOf(
          "ktlint_standard_filename" to "disabled",
          "ktlint_standard_trailing-comma-on-call-site" to "disabled",
          "ktlint_standard_trailing-comma-on-declaration-site" to "disabled"
        )
      )
  }
  kotlin {
    applyCommon()
    ktlint(libs.versions.ktlint.get())
      .editorConfigOverride(
        mapOf(
          "ktlint_standard_filename" to "disabled",
          "ktlint_standard_trailing-comma-on-call-site" to "disabled",
          "ktlint_standard_trailing-comma-on-declaration-site" to "disabled"
        )
      )
  }
}

tasks {
  named("idea") {
    notCompatibleWithConfigurationCache("https://github.com/gradle/gradle/issues/13480")
  }
}

idea {
  module {
    isDownloadJavadoc = true
    isDownloadSources = true
  }
}
