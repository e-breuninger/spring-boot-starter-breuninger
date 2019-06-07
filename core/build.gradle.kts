apply {
  from("$rootDir/gradle/libraries.gradle.kts")
}
val libraries = extra["libraries"] as Map<*, *>

dependencies {
  compile(libraries["spring-core"] as String)
  compile(libraries["unbescape"] as String)
  compile(libraries["owasp-java-html-sanitizer"] as String)
  compile(libraries["slugify"] as String)
}
