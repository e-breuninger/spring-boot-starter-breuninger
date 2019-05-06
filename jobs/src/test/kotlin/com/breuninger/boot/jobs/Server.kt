package com.breuninger.boot.jobs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.breuninger.boot"])
class Server

fun main(args: Array<String>) {
  runApplication<Server>(*args)
}
