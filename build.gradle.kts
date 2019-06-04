import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply {
  plugin("idea")
  plugin("org.jlleitschuh.gradle.ktlint")
  plugin("com.github.ben-manes.versions")
}

buildscript {
  repositories {
    maven { setUrl("https://plugins.gradle.org/m2/") }
  }
  project.apply {
    from("$rootDir/gradle/gradlePlugins.gradle.kts")
  }
  val gradlePlugins = extra["gradlePlugins"] as Map<*, *>
  dependencies {
    gradlePlugins.mapValues { classpath(it.value as String) }
  }
}

subprojects {
  apply {
    plugin("kotlin")
    plugin("kotlin-spring")
    plugin("io.spring.dependency-management")
  }

  group = "com.breuninger.boot"
  version = "3.0.0.RELEASE"

  configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_12
    targetCompatibility = JavaVersion.VERSION_12
  }

  repositories {
    jcenter()
  }

  tasks.withType<KotlinCompile> {
    kotlinOptions {
      freeCompilerArgs = listOf("-Xjsr305=strict")
      jvmTarget = org.gradle.api.JavaVersion.VERSION_1_8.toString()
    }
  }
}

// TODO(BS): fix compileTime, compileOnly, ... dependencies
// TODO(BS): add coverage kotlin jacoco
// TODO(BS): add https://github.com/arturbosch/detekt

// package.json
// TODO(BS): add linting js
// TODO(BS): add linting css
// TODO(BS): add testing js
// TODO(BS): add coverage js
