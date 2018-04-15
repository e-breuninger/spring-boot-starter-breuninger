package com.breuninger.boot.status.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.breuninger.boot.status.domain.TeamInfo;

@Configuration
@EnableConfigurationProperties(TeamInfoProperties.class)
public class TeamInfoConfiguration {

  @Bean
  @ConditionalOnMissingBean(TeamInfo.class)
  public TeamInfo teamInfo(final TeamInfoProperties teamInfoProperties) {
    return TeamInfo.teamInfo(teamInfoProperties);
  }
}
