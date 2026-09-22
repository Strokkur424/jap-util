import net.strokkur.gradle.RemapClassesTask

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
  maven("https://eldonexus.de/repository/maven-public/")
}

dependencies {
  compileOnly(libs.bundles.annotations)

  testImplementation(platform(libs.junit.bom))
  testImplementation(libs.junit.jupiter)
  testRuntimeOnly(libs.junit.platform)

  compileOnly(project(":internal-code-gen-annotations"))
  annotationProcessor(project(":internal-code-gen-processor"))
}

tasks {
  test {
    useJUnitPlatform()
    testLogging {
      events("skipped", "failed")
    }
  }
}

java {
  withSourcesJar()
  withJavadocJar()
}

tasks.withType<Javadoc> {
  val options = options as StandardJavadocDocletOptions
  options.addStringOption("Xdoclint:-missing", "-quiet")
}

// Class pre-processing with annotation processors is complicated, if you want to modify the file. This
// Gradle-heavy, but Java-safe way of doing that relies on having two compilation passes, doing some ASM
// bytecode rewriting on the class files to remove a generated package prefix, and then stitching those
// two passes back together into a single JAR file. Maybe it's a bit cursed, but it works wonderfully!

tasks {
  named<JavaCompile>("compileJava") {
    // For the regular Java compile, don't run annotation processors
    options.compilerArgs.add("-proc:none")
  }

  register<JavaCompile>("processMain") {
    description = "Run annotation processors to generate new sources."

    source = sourceSets.main.get().java
    classpath = sourceSets.main.get().compileClasspath
    options.annotationProcessorPath = configurations.getByName("annotationProcessor")

    options.compilerArgs.add("-proc:only")
    options.generatedSourceOutputDirectory = layout.buildDirectory.dir("generated/sources/annotationProcessor/main")
    destinationDirectory = layout.buildDirectory.dir("generated/sources/annotationProcessor/fakemain")
  }

  register<JavaCompile>("compileGenerated") {
    description = "Compiles generated sources from process task."
    dependsOn("processMain")

    source(layout.buildDirectory.dir("generated/sources/annotationProcessor/main"))

    classpath = sourceSets["main"].compileClasspath + sourceSets["main"].output
    destinationDirectory.set(
      layout.buildDirectory.dir("classes/generated/original")
    )

    options.compilerArgs.add("-proc:none")
  }

  register<RemapClassesTask>("remapGenerated") {
    description = "Remaps the generated sources so they match the source packages."
    dependsOn("compileGenerated")
    sourceDir = layout.buildDirectory.dir("classes/generated/original")
    targetDir = layout.buildDirectory.dir("classes/generated/processed")
  }

  register<Jar>("jarModifier") {
    description = "Creates a new JAR file with the modified files."
    dependsOn("remapGenerated")

    val processedDir = layout.buildDirectory.dir("classes/generated/processed")
    val processedPaths = processedDir
      .map { dir ->
        fileTree(dir).files
          .map { it.relativeTo(dir.asFile).invariantSeparatorsPath }
          .toSet()
      }

    from(sourceSets.main.get().output) {
      exclude { element ->
        val path = element.relativePath.segments.joinToString("/")
        path in processedPaths.get()
      }
    }

    from(layout.buildDirectory.dir("classes/generated/processed"))
    archiveClassifier.set("modified")
  }
}
