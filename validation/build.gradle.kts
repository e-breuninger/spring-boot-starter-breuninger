apply {
  from("$rootDir/gradle/libraries.gradle.kts")
}
val libraries = extra["libraries"] as Map<*, *>

dependencies {
  compileOnly(libraries["jackson-module-kotlin"] as String)

  compileOnly(libraries["spring-boot-starter-webflux"] as String)

  annotationProcessor(libraries["spring-boot-configuration-processor"] as String)

  testCompile(libraries["jackson-module-kotlin"] as String)

  testCompile(libraries["spring-boot-starter-webflux"] as String)

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
}
