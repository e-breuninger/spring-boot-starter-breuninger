import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply {
  plugin("idea")
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

  configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_12
    targetCompatibility = JavaVersion.VERSION_12
  }

  repositories {
    jcenter()
  }

  tasks {
    withType<KotlinCompile> {
      kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = org.gradle.api.JavaVersion.VERSION_1_8.toString()
      }
    }
  }
}

// TODO(BS): add linting
// TODO(BS): add testing
// TODO(BS): add coverage
// TODO(BS): add signing
// TODO(BS): add nexus release
