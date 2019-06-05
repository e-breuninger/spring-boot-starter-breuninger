import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply {
  plugin("idea")
  plugin("org.jlleitschuh.gradle.ktlint")
  plugin("com.github.ben-manes.versions")
  plugin("io.codearte.nexus-staging")
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

configure<io.codearte.gradle.nexus.NexusStagingExtension> {
  val sonatypeUsername: String by project
  val sonatypePassword: String by project
  username = sonatypeUsername
  password = sonatypePassword
  packageGroup = "com.breuninger"
}

tasks.register("publish") {
  subprojects.filter { it.tasks.any { task -> task.name == "publish" } }
    .forEach { dependsOn("${it.name}:publish") }
  finalizedBy("closeAndReleaseRepository")
}

subprojects {
  apply {
    plugin("kotlin")
    plugin("kotlin-spring")
    plugin("io.spring.dependency-management")
    plugin("maven-publish")
    plugin("signing")
  }

  group = "com.breuninger.boot"
  version = "3.0.3.RELEASE"

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

  tasks.register<Jar>("sourcesJar") {
    from(project.extensions.getByType(SourceSetContainer::class)["main"].allSource)
    archiveClassifier.set("sources")
  }

  tasks.register<Jar>("javadocJar") {
    from(project.tasks["javadoc"])
    archiveClassifier.set("javadoc")
  }

  configure<PublishingExtension> {
    repositories {
      maven {
        setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2")
        credentials {
          val sonatypeUsername: String by project
          val sonatypePassword: String by project
          username = sonatypeUsername
          password = sonatypePassword
        }
      }
    }
    publications {
      create<MavenPublication>("mavenJava") {
        from(project.components["java"])
        artifact(project.tasks["sourcesJar"])
        artifact(project.tasks["javadocJar"])
        pom {
          name.set("spring-boot-starter-breuninger-${project.name}")
          description.set("spring-boot-starter-breuninger")
          url.set("https://gitlab.breuni.de:bewerten/vertreiben/kotlin-spring-poc")
          licenses {
            license {
              name.set("The Apache License, Version 2.0")
              url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
          }
          developers {
            developer {
              id.set("bstemmildt")
              name.set("Benedikt Stemmildt")
              email.set("bene@breuninger.de")
            }
          }
          scm {
            connection.set("scm:git@gitlab.breuni.de:bewerten/vertreiben/kotlin-spring-poc.git")
            developerConnection.set("scm:git@gitlab.breuni.de:bewerten/vertreiben/kotlin-spring-poc.git")
            url.set("scm:git@gitlab.breuni.de:bewerten/vertreiben/kotlin-spring-poc.git")
          }
        }
      }
    }
  }

  configure<SigningExtension> {
    val publishing: PublishingExtension = project.extensions["publishing"] as PublishingExtension
    sign(publishing.publications["mavenJava"])
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
