package com.breuninger.boot.health.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.breuninger.boot.health.indicator.ApplicationHealthIndicator;
import com.breuninger.boot.health.indicator.GracefulShutdownHealthIndicator;
import com.breuninger.boot.health.indicator.GracefulShutdownProperties;

@Configuration
@EnableConfigurationProperties(GracefulShutdownProperties.class)
public class HealthConfiguration {

  @Bean
  @ConditionalOnProperty(prefix = "breuninger.gracefulshutdown", name = "enabled", havingValue = "true")
  public GracefulShutdownHealthIndicator gracefulShutdownHealthIndicator(final GracefulShutdownProperties properties) {
    return new GracefulShutdownHealthIndicator(properties);
  }

  @Bean
  public ApplicationHealthIndicator applicationHealthIndicator() {
    return new ApplicationHealthIndicator();
  }
}
