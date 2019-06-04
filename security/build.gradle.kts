plugins {
  `maven-publish`
  signing
}

apply {
  from("$rootDir/gradle/libraries.gradle.kts")
}
val libraries = extra["libraries"] as Map<*, *>

dependencies {
  compile(libraries["kotlin-stdlib-jre8"] as String)
  compile(libraries["kotlin-reflect"] as String)
}

tasks.register<Jar>("sourcesJar") {
  from(sourceSets.main.get().allSource)
  archiveClassifier.set("sources")
}

tasks.register<Jar>("javadocJar") {
  from(tasks.javadoc)
  archiveClassifier.set("javadoc")
}

publishing {
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
      from(components["java"])
      artifact(tasks["sourcesJar"])
      artifact(tasks["javadocJar"])
      pom {
        name.set("spring-boot-starter-breuninger-security")
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

signing {
  sign(publishing.publications["mavenJava"])
}
