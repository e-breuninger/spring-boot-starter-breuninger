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
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
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

// TODO add linting
// TODO add testing
// TODO add coverage
// TODO add signing
// TODO add nexus release