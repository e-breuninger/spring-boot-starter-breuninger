project.apply {
  from("$rootDir/gradle/core.gradle.kts")
}
val coreVersions = extra["coreVersions"] as Map<*, *>

val libraryVersions = mapOf(
  "jackson-module-kotlin" to "2.9.8",

  "spring-boot-admin-starter" to "2.1.3",

  "edison-hal" to "2.0.2",

  "togglz" to "2.6.1.Final",

  "reactor-kafka" to "1.1.0.RELEASE",

  "unbescape" to "1.1.6.RELEASE",
  "owasp-java-html-sanitizer" to "20181114.1",
  "slugify" to "2.3"
)

val libraries = mapOf(
  "kotlin-stdlib-jre8" to "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${coreVersions["kotlin"]}",
  "kotlin-reflect" to "org.jetbrains.kotlin:kotlin-reflect:${coreVersions["kotlin"]}",
  "jackson-module-kotlin" to "com.fasterxml.jackson.module:jackson-module-kotlin:${libraryVersions["jackson-module-kotlin"]}",

  "spring-core" to "org.springframework:spring-core:${coreVersions["spring"]}",
  "spring-boot-starter-aop" to "org.springframework.boot:spring-boot-starter-aop:${coreVersions["spring-boot"]}",
  "spring-boot-starter-actuator" to "org.springframework.boot:spring-boot-starter-actuator:${coreVersions["spring-boot"]}",
  "spring-boot-starter-webflux" to "org.springframework.boot:spring-boot-starter-webflux:${coreVersions["spring-boot"]}",
  "spring-boot-starter-data-mongodb-reactive" to "org.springframework.boot:spring-boot-starter-data-mongodb-reactive:${coreVersions["spring-boot"]}",

  "spring-boot-admin-starter-server" to "de.codecentric:spring-boot-admin-starter-server:${libraryVersions["spring-boot-admin-starter"]}",
  "spring-boot-admin-starter-client" to "de.codecentric:spring-boot-admin-starter-client:${libraryVersions["spring-boot-admin-starter"]}",

  "edison-hal" to "de.otto.edison:edison-hal:${libraryVersions["edison-hal"]}",

  "togglz-spring-core" to "org.togglz:togglz-spring-core:${libraryVersions["togglz"]}",

  "reactor-kafka" to "io.projectreactor.kafka:reactor-kafka:${libraryVersions["reactor-kafka"]}",

  "unbescape" to "org.unbescape:unbescape:${libraryVersions["unbescape"]}",
  "owasp-java-html-sanitizer" to "com.googlecode.owasp-java-html-sanitizer:owasp-java-html-sanitizer:${libraryVersions["owasp-java-html-sanitizer"]}",
  "slugify" to "com.github.slugify:slugify:${libraryVersions["slugify"]}",

  "spring-boot-devtools" to "org.springframework.boot:spring-boot-devtools:${coreVersions["spring-boot"]}",
  "spring-context-indexer" to "org.springframework:spring-context-indexer:${coreVersions["spring"]}",
  "spring-boot-configuration-processor" to "org.springframework.boot:spring-boot-configuration-processor:${coreVersions["spring-boot"]}"
)

extra["libraryVersions"] = libraryVersions
extra["libraries"] = libraries
