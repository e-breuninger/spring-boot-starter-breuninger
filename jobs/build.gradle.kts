import com.moowork.gradle.node.npm.NpmTask

apply {
  plugin("com.moowork.node")
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
  compile(libraries["spring-boot-starter-actuator"] as String)
  compile(libraries["spring-boot-starter-data-mongodb-reactive"] as String)
  testCompile(libraries["junit-jupiter-api"] as String)
  testCompile(libraries["junit-jupiter-engine"] as String)

  compile(libraries["spring-boot-starter-aop"] as String)

  annotationProcessor(libraries["spring-boot-configuration-processor"] as String)
}

task<NpmTask>("buildJs") {
  dependsOn("npmInstall")
  setArgs(listOf("run", "build"))
}

tasks.getByName("build")
  .dependsOn("buildJs")

tasks {
  jar {
    from ("src/main/resources/META-INF") {
      into ("META-INF")
    }
  }
}
