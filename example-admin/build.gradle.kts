import org.springframework.boot.gradle.tasks.run.BootRun

apply {
  plugin("org.springframework.boot")

  from("$rootDir/gradle/libraries.gradle.kts")
}
val libraries = extra["libraries"] as Map<*, *>

dependencies {
  compile(libraries["kotlin-stdlib-jre8"] as String)
  compile(libraries["kotlin-reflect"] as String)
  compile(libraries["jackson-module-kotlin"] as String)

  compile(libraries["spring-boot-admin-starter-server"] as String)
  compile(libraries["spring-boot-starter-webflux"] as String)
  compile(project(":togglz"))
  compile(project(":jobs"))

  compileOnly(libraries["spring-context-indexer"] as String)
}

tasks {
  withType<BootRun> {
    systemProperties = System.getProperties().mapKeys { it.key.toString() }.toMap()
  }
}
