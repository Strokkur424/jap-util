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

tasks.withType(Javadoc::class.java).configureEach {
  val options = options as StandardJavadocDocletOptions
  options.addStringOption("Xdoclint:all,-missing", "-quiet")
}
