package com.breuninger.boot.logging.ui;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.breuninger.boot")
public class LoggingTestServer {

  public static void main(final String... args) {
    run(LoggingTestServer.class, args);
  }
}
