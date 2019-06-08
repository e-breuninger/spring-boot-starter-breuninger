apply {
  from("$rootDir/gradle/libraries.gradle.kts")
}
val libraries = extra["libraries"] as Map<*, *>

dependencies {
  compile(libraries["unbescape"] as String)
  compile(libraries["owasp-java-html-sanitizer"] as String)
  compile(libraries["slugify"] as String)

  compileOnly(libraries["spring-core"] as String)

  testCompile(libraries["spring-core"] as String)

  testCompile(libraries["junit-jupiter-api"] as String)
  testCompile(libraries["junit-jupiter-engine"] as String)
  testCompile(libraries["assertk"] as String)
}
