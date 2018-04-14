package com.breuninger.boot.testsupport;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.breuninger.boot")
public class TestServer {

  private static ApplicationContext ctx;

  public static ApplicationContext applicationContext() {
    return ctx;
  }

  public static void main(final String... args) {
    ctx = run(TestServer.class, args);
  }
}
