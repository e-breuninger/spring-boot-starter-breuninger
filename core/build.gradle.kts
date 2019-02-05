apply {
  from("$rootDir/gradle/libraries.gradle.kts")
}
val libraries = extra["libraries"] as Map<*, *>

dependencies {
  compile(libraries["kotlin-stdlib-jre8"] as String)
  compile(libraries["kotlin-reflect"] as String)

  compile(libraries["spring-core"] as String)
  compile(libraries["unbescape"] as String)
  compile(libraries["owasp-java-html-sanitizer"] as String)
  compile(libraries["slugify"] as String)
}
