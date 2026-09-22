plugins {
  `kotlin-dsl`
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.ow2.asm:asm:9.10.1")
  implementation("org.ow2.asm:asm-commons:9.10.1")
}

kotlin {
  jvmToolchain(21)
}
