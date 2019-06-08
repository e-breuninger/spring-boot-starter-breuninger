import com.moowork.gradle.node.yarn.YarnTask

apply {
  plugin("com.moowork.node")
}

apply {
  from("$rootDir/gradle/libraries.gradle.kts")
}
val libraries = extra["libraries"] as Map<*, *>

dependencies {
  compileOnly(libraries["jackson-module-kotlin"] as String)

  compile(libraries["togglz-spring-core"] as String)

  compileOnly(libraries["spring-boot-starter-webflux"] as String)
  compileOnly(libraries["spring-boot-starter-thymeleaf"] as String)
  compileOnly(libraries["spring-boot-starter-actuator"] as String)
  compileOnly(libraries["spring-boot-starter-data-mongodb-reactive"] as String)

  testCompile(libraries["jackson-module-kotlin"] as String)

  testCompile(libraries["spring-boot-starter-webflux"] as String)
  testCompile(libraries["spring-boot-starter-thymeleaf"] as String)
  testCompile(libraries["spring-boot-starter-actuator"] as String)
  testCompile(libraries["spring-boot-starter-data-mongodb-reactive"] as String)

  testCompile(libraries["junit-jupiter-api"] as String)
  testCompile(libraries["junit-jupiter-engine"] as String)
  testCompile(libraries["mockk"] as String)
  testCompile(libraries["assertk"] as String)
  testCompile(libraries["spring-boot-starter-test"] as String) {
    exclude(group = "junit")
    exclude(group = "org.mockito")
    exclude(group = "org.hamcrest")
    exclude(group = "org.assertj")
  }
  testCompile(libraries["flapdoodle"] as String)

  annotationProcessor(libraries["spring-boot-configuration-processor"] as String)
}

tasks.register<YarnTask>("yarnInstall") {
  setArgs(listOf("install"))
}

// TODO(BS): clean js-build dir before building new

tasks.register<YarnTask>("buildJs") {
  dependsOn("yarnInstall")
  setArgs(listOf("build"))
}

tasks.build {
  dependsOn("buildJs")
}

tasks.jar {
  from("src/main/resources/META-INF") {
    into("META-INF")
  }
}
