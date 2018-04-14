package com.breuninger.boot.example;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@PropertySource("version.properties")
@ComponentScan("com.breuninger.boot")
public class ExampleTogglzMongoServer {

  public static void main(final String... args) {
    run(ExampleTogglzMongoServer.class, args);
  }
}
