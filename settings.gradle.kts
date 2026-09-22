pluginManagement {
  repositories {
    gradlePluginPortal()
    maven("https://eldonexus.de/repository/maven-public/")
  }
}

//includeBuild("build-src")

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "jap-util"

fun includeAll(dir: String, name: (String) -> String = { it }) {
  File(rootDir, dir).listFiles()?.forEach {
    include(name(it.name))
    project(":${name(it.name)}").projectDir = it
  }
}

includeAll("modules")
includeAll("internal") { "internal-$it" }
includeAll("test-ap") { "test-ap-$it" }
