plugins {
  id("checkstyle")
  id("maven-publish")
  alias(libs.plugins.conventions.java)
  alias(libs.plugins.conventions.spotless)
}

license.useMIT()

repositories {
  mavenCentral()
}

dependencies {
  compileOnly(libs.bundles.annotations)
}
