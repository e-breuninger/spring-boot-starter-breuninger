apply {
  from("$rootDir/gradle/libraries.gradle.kts")
}
val libraries = extra["libraries"] as Map<*, *>

dependencies {
  compile(libraries["kotlin-stdlib-jre8"] as String)
  compile(libraries["kotlin-reflect"] as String)

  compile(libraries["togglz-spring-core"] as String)

  compile(libraries["spring-boot-starter-actuator"] as String)
  compile(libraries["spring-boot-starter-data-mongodb-reactive"] as String)

  annotationProcessor(libraries["spring-boot-configuration-processor"] as String)
}
