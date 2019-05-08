import org.springframework.boot.gradle.tasks.run.BootRun

apply {
  plugin("org.springframework.boot")
  plugin("com.gorylenko.gradle-git-properties")

  from("$rootDir/gradle/libraries.gradle.kts")
}
val libraries = extra["libraries"] as Map<*, *>

dependencies {
  compile(libraries["kotlin-stdlib-jre8"] as String)
  compile(libraries["kotlin-reflect"] as String)
  compile(libraries["jackson-module-kotlin"] as String)

  compile(libraries["spring-boot-admin-starter-client"] as String)
  compile(project(":core"))
  compile(project(":jobs"))
  compile(project(":togglz"))
  compile(project(":validation"))

  compile(libraries["reactor-kafka"] as String)

  compile(libraries["spring-boot-starter-thymeleaf"] as String)

  compile(libraries["spring-boot-devtools"] as String)
  compileOnly(libraries["spring-context-indexer"] as String)
}

tasks {
  withType<BootRun> {
    systemProperties = System.getProperties().mapKeys { it.key.toString() }.toMap()
  }
}
