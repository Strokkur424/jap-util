import net.strokkur.convention.JavaConvention
import net.strokkur.convention.SpotlessConvention

plugins {
  id("net.strokkur.conventions-java") version "0.1.0" apply false
  id("net.strokkur.conventions-spotless") version "0.1.0" apply false
}

tasks.updateDaemonJvm {
  languageVersion = JavaLanguageVersion.of(25)
  vendor = JvmVendorSpec.ORACLE
}

subprojects {
  plugins.apply(MavenPublishPlugin::class.java)
  plugins.apply(CheckstylePlugin::class.java)
  plugins.apply(JavaConvention::class.java)
  plugins.apply(SpotlessConvention::class.java)

  extensions.configure(SpotlessConvention.LicenseExtension::class.java) {
    useMIT()
  }
}
