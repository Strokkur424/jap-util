plugins {
  id("checkstyle")
  alias(libs.plugins.conventions.java)
  alias(libs.plugins.conventions.spotless)
  id("com.gradleup.shadow") version "9.1.0"
}

license.useMIT()
strokkConventions.javaVersion = 21

repositories {
  mavenCentral()
  maven("https://eldonexus.de/repository/maven-public/")
}

dependencies {
  implementation(project(":internal-code-gen-annotations"))
  implementation("net.strokkur.japutil:source-map:0.1.4-SNAPSHOT")
  implementation("net.strokkur.japutil:code-gen:0.1.4-SNAPSHOT")
  compileOnly(libs.auto.service.annotations)
  annotationProcessor(libs.auto.service)
}

configurations {
  named("apiElements") {
    outgoing.artifacts.clear()
    outgoing.variants.clear()
    outgoing.artifact(tasks.named("shadowJar"))
  }

  named("runtimeElements") {
    outgoing.artifacts.clear()
    outgoing.variants.clear()
    outgoing.artifact(tasks.named("shadowJar"))
  }
}
