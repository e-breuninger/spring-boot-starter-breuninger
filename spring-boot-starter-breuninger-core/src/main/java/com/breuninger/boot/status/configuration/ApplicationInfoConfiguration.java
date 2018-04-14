package com.breuninger.boot.status.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.breuninger.boot.configuration.BreuningerApplicationProperties;
import com.breuninger.boot.status.domain.ApplicationInfo;

@Configuration
@EnableConfigurationProperties(BreuningerApplicationProperties.class)
public class ApplicationInfoConfiguration {

  @Value("${spring.application.name:unknown}")
  private String serviceName;

  @Bean
  @ConditionalOnMissingBean(ApplicationInfo.class)
  public ApplicationInfo applicationInfo(final BreuningerApplicationProperties breuningerApplicationProperties) {
    return ApplicationInfo.applicationInfo(serviceName, breuningerApplicationProperties);
  }
}
