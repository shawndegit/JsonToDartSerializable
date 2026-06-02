plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.9.0"
  id("org.jetbrains.intellij") version "1.15.0"
}

group = "com.nesprasit"
version = "1.0.1"

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

// Configure Gradle IntelliJ Plugin
intellij {
  pluginName.set("JsonToDartSerializable")

  // Build against IntelliJ IDEA platform so plugin can be installed in IDEA and Android Studio
  type.set("IC")
  version.set("2022.3.3") // 223 baseline

  // Keep Dart dependency; avoid Android-only dependency for cross-IDE compatibility
  plugins.set(listOf("Dart:223.8977"))
}

tasks {

  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
    options.encoding = "UTF-8"
  }

  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
  }

  patchPluginXml {
    version.set("1.0.1")
    sinceBuild.set("223") // 2022.3+
    untilBuild.set("")
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
  }

  test {
    useJUnitPlatform()
  }
}

tasks.register("buildRelease", type = Copy::class) {
  dependsOn(tasks["buildPlugin"])

  val dir = System.getProperty("user.dir")
  val from = file("$dir/build/libs/")
  val target = file("$dir/buildLib/libs/")

  from.listFiles()?.findLast { it.name.contains(Regex("^Json.*")) }?.let {
    from(it)
    into(target)
  }

  dependsOn(":buildLib:buildZip")
}
