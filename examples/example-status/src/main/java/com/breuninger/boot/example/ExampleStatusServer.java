package com.breuninger.boot.example;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@PropertySource("version.properties")
@SpringBootApplication(scanBasePackages = "com.breuninger.boot")
public class ExampleStatusServer {

  public static void main(final String... args) {
    run(ExampleStatusServer.class, args);
  }
}
