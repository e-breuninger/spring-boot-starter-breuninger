package com.breuninger.boot.jobs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.breuninger.boot"])
class SpringBootTestServer

fun main(args: Array<String>) {
  runApplication<SpringBootTestServer>(*args)
}
