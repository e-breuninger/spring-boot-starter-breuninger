package com.breuninger.boot.status.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.breuninger.boot.status.domain.VersionInfo;

@Configuration
@EnableConfigurationProperties(VersionInfoProperties.class)
public class VersionInfoConfiguration {

  private final GitProperties gitProperties;

  public VersionInfoConfiguration(@Autowired(required = false) final GitProperties gitProperties) {
    this.gitProperties = gitProperties;
  }

  @Bean
  @ConditionalOnMissingBean(VersionInfo.class)
  public VersionInfo gitInfo(final VersionInfoProperties versionInfoProperties) {
    return VersionInfo.versionInfo(versionInfoProperties, gitProperties);
  }
}
