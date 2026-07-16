plugins {
  id("checkstyle")
  id("maven-publish")
  alias(libs.plugins.conventions.java)
  alias(libs.plugins.conventions.spotless)
}

license.useMIT()
strokkConventions.javaVersion = 21

repositories {
  mavenCentral()
}

dependencies {
  compileOnly(libs.bundles.annotations)
  api(project(":code-gen"))
}

java {
  withSourcesJar()
  withJavadocJar()
}
