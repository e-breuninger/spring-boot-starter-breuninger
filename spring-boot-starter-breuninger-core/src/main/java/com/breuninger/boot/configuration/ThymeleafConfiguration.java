package com.breuninger.boot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import nz.net.ultraq.thymeleaf.LayoutDialect;

@Configuration
public class ThymeleafConfiguration {

  @Bean
  LayoutDialect layoutDialect() {
    return new LayoutDialect();
  }
}
