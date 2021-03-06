import org.springframework.boot.gradle.tasks.run.BootRun

apply {
  plugin("org.springframework.boot")
  plugin("com.gorylenko.gradle-git-properties")

  from("$rootDir/gradle/libraries.gradle.kts")
}
val libraries = extra["libraries"] as Map<*, *>

dependencies {
  compile(libraries["jackson-module-kotlin"] as String)

  compile(libraries["spring-boot-admin-starter-server"] as String)
  compile(libraries["spring-boot-starter-webflux"] as String)
  compile(project(":togglz"))
  compile(project(":jobs"))

  compile(libraries["spring-boot-devtools"] as String)
  compileOnly(libraries["spring-context-indexer"] as String)
}

tasks.withType<BootRun> {
  systemProperties = System.getProperties().mapKeys { it.key.toString() }.toMap()
}
