import com.gradle.scan.plugin.BuildScanExtension
import io.codearte.gradle.nexus.NexusStagingExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply {
  plugin("com.gradle.build-scan")
  plugin("idea")
  plugin("io.gitlab.arturbosch.detekt")
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

repositories {
  jcenter()
}

apply {
  from("$rootDir/gradle/gradlePlugins.gradle.kts")
  from("$rootDir/gradle/libraries.gradle.kts")
}
val gradlePluginVersions = extra["gradlePluginVersions"] as Map<*, *>
val libraries = extra["libraries"] as Map<*, *>

dependencies {
  "detektPlugins"(libraries["detekt-formatting"] as String)
}

configure<BuildScanExtension> {
  termsOfServiceUrl = "https://gradle.com/terms-of-service"
  termsOfServiceAgree = "yes"
}

configure<DetektExtension> {
  toolVersion = gradlePluginVersions["detekt"] as String
  config = files("$rootDir/detekt.yml")
}

tasks.register("publish") {
  subprojects.forEach { dependsOn("${it.name}:publish") }
  finalizedBy("closeAndReleaseRepository")
}

configure<NexusStagingExtension> {
  val sonatypeUsername: String by project
  val sonatypePassword: String by project
  username = sonatypeUsername
  password = sonatypePassword
  packageGroup = "com.breuninger"
}

subprojects {
  apply {
    plugin("io.spring.dependency-management")
    plugin("kotlin")
    plugin("kotlin-spring")
    plugin("io.gitlab.arturbosch.detekt")
    plugin("jacoco")
    plugin("org.jetbrains.dokka")
    plugin("maven-publish")
    plugin("signing")
  }

  group = "com.breuninger.boot"
  version = "3.0.5.RELEASE"

  repositories {
    jcenter()
  }

  apply {
    from("$rootDir/gradle/gradlePlugins.gradle.kts")
    from("$rootDir/gradle/libraries.gradle.kts")
  }
  val gradlePluginVersions = extra["gradlePluginVersions"] as Map<*, *>
  val libraries = extra["libraries"] as Map<*, *>

  dependencies {
    "compile"(libraries["kotlin-stdlib-jre8"] as String)
    "compile"(libraries["kotlin-reflect"] as String)

    "detektPlugins"(libraries["detekt-formatting"] as String)
  }

  configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_12
    targetCompatibility = JavaVersion.VERSION_12
  }

  tasks.withType<KotlinCompile> {
    kotlinOptions {
      jvmTarget = org.gradle.api.JavaVersion.VERSION_1_8.toString()
      freeCompilerArgs = listOf("-Xjsr305=strict",
        "-progressive",
        "-Xskip-runtime-version-check",
        "-Xdisable-default-scripting-plugin",
        "-Xuse-experimental=kotlin.Experimental")
    }
  }

  configure<DetektExtension> {
    toolVersion = gradlePluginVersions["detekt"] as String
    config = files("$rootDir/detekt.yml")
  }

  tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
      events = setOf(
        TestLogEvent.FAILED,
        TestLogEvent.PASSED,
        TestLogEvent.SKIPPED,
        TestLogEvent.STANDARD_OUT
      )
      exceptionFormat = TestExceptionFormat.FULL
      showExceptions = true
      showCauses = true
      showStackTraces = true
    }
    finalizedBy("jacocoTestReport")
  }

  configure<JacocoPluginExtension> {
    toolVersion = gradlePluginVersions["jacoco"] as String
  }

  tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn("test")
    reports {
      html.isEnabled = true
      xml.isEnabled = false
      csv.isEnabled = false
    }
  }

  tasks.register<Jar>("sourcesJar") {
    from(project.the<SourceSetContainer>()["main"].allSource)
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
          url.set("https://github.com/e-breuninger/spring-boot-starter-breuninger")
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
            connection.set("scm:git@github.com:e-breuninger/spring-boot-starter-breuninger.git")
            developerConnection.set("scm:git@github.com:e-breuninger/spring-boot-starter-breuninger.git")
            url.set("scm:git@github.com:e-breuninger/spring-boot-starter-breuninger.git")
          }
        }
      }
    }
  }

  configure<SigningExtension> {
    val publishing = project.extensions.findByType<PublishingExtension>()
    sign(publishing!!.publications["mavenJava"])
  }
}

// package.json
// TODO(BS): add linting js
// TODO(BS): add linting css
// TODO(BS): add testing js
// TODO(BS): add coverage js

// commitizen			3.0.7	❯	3.1.1
// tslint      			5.14.0	❯	5.17.0
// @commitlint/cli    		7.5.2	❯	8.0.0
// @commitlint/prompt		7.5.0	❯	8.0.0
// husky         		1.3.1	❯	2.3.0
// stylelint           		9.10.1	❯	10.0.1
// typescript-tslint-plugin  	0.3.1	❯	0.4.0
