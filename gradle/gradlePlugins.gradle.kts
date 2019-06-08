project.apply {
  from("$rootDir/gradle/core.gradle.kts")
}
val coreVersions = extra["coreVersions"] as Map<*, *>

val gradlePluginVersions = mapOf(
  "git-properties" to "2.0.0",

  "node" to "1.3.1",

  "detekt" to "1.0.0-RC15",
  "jacoco" to "0.8.3",
  "dokka" to "0.9.18",

  "nexus-staging" to "0.21.0",
  "versions" to "0.21.0",

  "build-scan" to "2.3"
)

val gradlePlugins = mapOf(
  "spring-boot" to "org.springframework.boot:spring-boot-gradle-plugin:${coreVersions["spring-boot"]}",
  "kotlin-gradle-plugin" to "org.jetbrains.kotlin:kotlin-gradle-plugin:${coreVersions["kotlin"]}",
  "kotlin-allopen" to "org.jetbrains.kotlin:kotlin-allopen:${coreVersions["kotlin"]}",

  "git-properties" to "gradle.plugin.com.gorylenko.gradle-git-properties:gradle-git-properties:${gradlePluginVersions["git-properties"]}",

  "node" to "com.moowork.gradle:gradle-node-plugin:${gradlePluginVersions["node"]}",
  "detekt" to "io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${gradlePluginVersions["detekt"]}",
  "dokka" to "org.jetbrains.dokka:dokka-gradle-plugin:${gradlePluginVersions["dokka"]}",

  "nexus-staging" to "io.codearte.gradle.nexus:gradle-nexus-staging-plugin:${gradlePluginVersions["nexus-staging"]}",
  "versions" to "com.github.ben-manes:gradle-versions-plugin:${gradlePluginVersions["versions"]}",

  "build-scan" to "com.gradle:build-scan-plugin:${gradlePluginVersions["build-scan"]}"
)

extra["gradlePluginVersions"] = gradlePluginVersions
extra["gradlePlugins"] = gradlePlugins
