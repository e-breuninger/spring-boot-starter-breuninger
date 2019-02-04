project.apply {
  from("$rootDir/gradle/core.gradle.kts")
}
val coreVersions = extra["coreVersions"] as Map<*, *>

val libraryVersions = mapOf(
  "jackson-module-kotlin" to "2.9.8",

  "spring-boot-admin-starter" to "2.1.2",

  "edison-hal" to "2.0.2",

  "togglz" to "2.6.1.Final",

  "reactor-kafka" to "1.1.0.RELEASE",

  "hibernate-validator" to "6.0.14.Final",

  "spring" to "5.1.4.RELEASE"
)

val libraries = mapOf(
  "kotlin-stdlib-jre8" to "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${coreVersions["kotlin"]}",
  "kotlin-reflect" to "org.jetbrains.kotlin:kotlin-reflect:${coreVersions["kotlin"]}",
  "jackson-module-kotlin" to "com.fasterxml.jackson.module:jackson-module-kotlin:${libraryVersions["jackson-module-kotlin"]}",

  "spring-boot-starter-actuator" to "org.springframework.boot:spring-boot-starter-actuator:${coreVersions["spring-boot"]}",
  "spring-boot-starter-webflux" to "org.springframework.boot:spring-boot-starter-webflux:${coreVersions["spring-boot"]}",
  "spring-boot-starter-data-mongodb-reactive" to "org.springframework.boot:spring-boot-starter-data-mongodb-reactive:${coreVersions["spring-boot"]}",

  "spring-boot-admin-starter-server" to "de.codecentric:spring-boot-admin-starter-server:${libraryVersions["spring-boot-admin-starter"]}",
  "spring-boot-admin-starter-client" to "de.codecentric:spring-boot-admin-starter-client:${libraryVersions["spring-boot-admin-starter"]}",

  "edison-hal" to "de.otto.edison:edison-hal:${libraryVersions["edison-hal"]}",

  "togglz-spring-core" to "org.togglz:togglz-spring-core:${libraryVersions["togglz"]}",

  "reactor-kafka" to "io.projectreactor.kafka:reactor-kafka:${libraryVersions["reactor-kafka"]}",

  "hibernate-validator" to "org.hibernate:hibernate-validator:${libraryVersions["hibernate-validator"]}",

  "spring-boot-devtools" to "org.springframework.boot:spring-boot-devtools:${coreVersions["spring-boot"]}",
  "spring-context-indexer" to "org.springframework:spring-context-indexer:${libraryVersions["spring"]}",
  "spring-boot-configuration-processor" to "org.springframework.boot:spring-boot-configuration-processor:${coreVersions["spring-boot"]}"
)

extra["libraryVersions"] = libraryVersions
extra["libraries"] = libraries
