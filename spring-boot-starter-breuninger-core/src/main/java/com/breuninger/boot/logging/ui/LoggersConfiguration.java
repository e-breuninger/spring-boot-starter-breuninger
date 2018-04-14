package com.breuninger.boot.logging.ui;

import org.springframework.boot.actuate.logging.LoggersEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.breuninger.boot.configuration.BreuningerApplicationProperties;
import com.breuninger.boot.navigation.NavBar;

@Configuration
@ConditionalOnProperty(name = "endpoints.loggers.enabled", havingValue = "true")
@EnableConfigurationProperties(BreuningerApplicationProperties.class)
public class LoggersConfiguration {

  @Bean
  @ConditionalOnProperty(prefix = "breuninger.logging.ui", name = "enabled", matchIfMissing = true)
  public static DisableEndpointPostProcessor loggersPropertySource() {
    return new DisableEndpointPostProcessor("loggers");
  }

  @Bean
  @ConditionalOnProperty(prefix = "breuninger.logging.ui", name = "enabled", matchIfMissing = true)
  public LoggersController loggersHtmlEndpoint(final LoggersEndpoint loggersEndpoint, final NavBar rightNavBar,
                                               final BreuningerApplicationProperties properties) {
    return new LoggersController(loggersEndpoint, rightNavBar, properties);
  }
}
