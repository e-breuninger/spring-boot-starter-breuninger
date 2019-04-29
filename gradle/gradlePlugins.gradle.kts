project.apply {
  from("$rootDir/gradle/core.gradle.kts")
}
val coreVersions = extra["coreVersions"] as Map<*, *>

val gradlePluginVersions = mapOf(
  "versions" to "0.21.0",

  "node" to "1.3.1"
)

val gradlePlugins = mapOf(
  "spring-boot" to "org.springframework.boot:spring-boot-gradle-plugin:${coreVersions["spring-boot"]}",
  "kotlin-gradle-plugin" to "org.jetbrains.kotlin:kotlin-gradle-plugin:${coreVersions["kotlin"]}",
  "kotlin-allopen" to "org.jetbrains.kotlin:kotlin-allopen:${coreVersions["kotlin"]}",

  "versions" to "com.github.ben-manes:gradle-versions-plugin:${gradlePluginVersions["versions"]}",

  "node" to "com.moowork.gradle:gradle-node-plugin:${gradlePluginVersions["node"]}"
)

extra["gradlePluginVersions"] = gradlePluginVersions
extra["gradlePlugins"] = gradlePlugins
