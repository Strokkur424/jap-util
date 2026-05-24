plugins {
  alias(libs.plugins.conventions.java)
  alias(libs.plugins.conventions.spotless)
}

license.useMIT()
strokkConventions.javaVersion = 21

repositories {
  mavenCentral()
}

dependencies {
  api(libs.bundles.annotations)

  api(project(":test-ap-processor"))
  annotationProcessor(project(":test-ap-processor"))
}
