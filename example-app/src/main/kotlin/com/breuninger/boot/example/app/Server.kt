package com.breuninger.boot.example.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.breuninger.boot"])
class Server

fun main(args: Array<String>) {
  runApplication<Server>(*args)
}
