package com.breuninger.boot.example;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.breuninger.boot")
public class ExampleMetricsServer {

  public static void main(final String... args) {
    run(ExampleMetricsServer.class, args);
  }
}
