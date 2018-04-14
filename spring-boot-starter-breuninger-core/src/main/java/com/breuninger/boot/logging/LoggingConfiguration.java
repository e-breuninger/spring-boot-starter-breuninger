package com.breuninger.boot.logging;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LoggingProperties.class)
public class LoggingConfiguration {

  @Bean
  @ConditionalOnProperty(prefix = "breuninger.logging", name = "header.enabled")
  public LogHeadersToMDCFilter logHeadersToMDCFilter(final LoggingProperties properties) {
    return new LogHeadersToMDCFilter(Arrays.asList(properties.getHeader().getNames().split(",")));
  }
}
