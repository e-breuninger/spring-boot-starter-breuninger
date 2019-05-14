import com.moowork.gradle.node.yarn.YarnTask

apply {
  plugin("com.moowork.node")
}

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
  compile(libraries["jackson-module-kotlin"] as String)

  compile(libraries["spring-boot-starter-webflux"] as String)
  compile(libraries["spring-boot-starter-thymeleaf"] as String)
  compile(libraries["spring-boot-starter-actuator"] as String)
  compile(libraries["spring-boot-starter-data-mongodb-reactive"] as String)

  testCompile(libraries["junit-jupiter-api"] as String)
  testCompile(libraries["junit-jupiter-engine"] as String)
  testCompile(libraries["junit-jupiter-params"] as String)
  testCompile(libraries["mockk"] as String)
  testCompile(libraries["assertk"] as String)
  testCompile(libraries["spring-boot-test"] as String)
  testCompile(libraries["flapdoodle"] as String)

  annotationProcessor(libraries["spring-boot-configuration-processor"] as String)
}

task<YarnTask>("yarnInstall") {
  setArgs(listOf("install"))
}

task<YarnTask>("buildJs") {
  dependsOn("yarnInstall")
  setArgs(listOf("build"))
}

tasks.getByName("build")
  .dependsOn("buildJs")

tasks {
  jar {
    from("src/main/resources/META-INF") {
      into("META-INF")
    }
  }
}

publishing {
  repositories {
    maven {
      setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2")
      credentials {
        username = System.getenv("sonatypeUsername")
        password = System.getenv("sonatypePassword")
      }
    }
  }
  publications {
    create<MavenPublication>("mavenJava") {
      from(components["java"])
    }
  }
}

signing {
  sign(publishing.publications["mavenJava"])
}
