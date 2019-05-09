package com.breuninger.boot.togglz

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.breuninger.boot.togglz"])
class SpringBootTestServer

fun main(args: Array<String>) {
  runApplication<SpringBootTestServer>(*args)
}
