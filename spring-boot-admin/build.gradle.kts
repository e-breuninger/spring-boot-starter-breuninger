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
}

task<NpmTask>("buildJs") {
  dependsOn("npmInstall")
  setArgs(listOf("run", "build"))
}

tasks.getByName("build")
  .dependsOn("buildJs")

