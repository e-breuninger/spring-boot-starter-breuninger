package com.breuninger.boot.example.admin

import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableAdminServer
class Server

fun main(args: Array<String>) {
  runApplication<Server>(*args)
}
