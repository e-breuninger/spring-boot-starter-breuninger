package com.breuninger.boot.acceptance.status;

import static com.breuninger.boot.status.configuration.TeamInfoProperties.teamInfoProperties;
import static com.breuninger.boot.status.configuration.VersionInfoProperties.versionInfoProperties;
import static com.breuninger.boot.status.domain.ServiceDependencyBuilder.restServiceDependency;
import static com.breuninger.boot.status.domain.StatusDetail.statusDetail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.breuninger.boot.configuration.BreuningerApplicationProperties;
import com.breuninger.boot.status.domain.ApplicationInfo;
import com.breuninger.boot.status.domain.Criticality;
import com.breuninger.boot.status.domain.Level;
import com.breuninger.boot.status.domain.ServiceDependency;
import com.breuninger.boot.status.domain.Status;
import com.breuninger.boot.status.domain.TeamInfo;
import com.breuninger.boot.status.domain.VersionInfo;
import com.breuninger.boot.status.indicator.MutableStatusDetailIndicator;
import com.breuninger.boot.status.indicator.StatusDetailIndicator;

@Configuration
public class StatusAcceptanceConfiguration {

  @Bean
  ApplicationInfo applicationInfo() {
    return ApplicationInfo.applicationInfo("test-app",
      BreuningerApplicationProperties.breuningerApplicationProperties("Some Test", "test-group", "test-env", "desc"));
  }

  @Bean
  TeamInfo teamInfo() {
    return TeamInfo.teamInfo(teamInfoProperties("Test Team", "technical@example.org", "business@example.org"));
  }

  @Bean
  VersionInfo versionInfo() {
    return VersionInfo.versionInfo(versionInfoProperties("1.0.0", "ab1234", "http://example.org/vcs/{version}"));
  }

  @Bean
  StatusDetailIndicator fooIndicator() {
    return new MutableStatusDetailIndicator(statusDetail("foo", Status.OK, "test ok"));
  }

  @Bean
  StatusDetailIndicator barIndicator() {
    return new MutableStatusDetailIndicator(statusDetail("bar", Status.WARNING, "test warning"));
  }

  @Bean
  Criticality criticality() {
    return Criticality.criticality(Level.LOW, "some impact");
  }

  @Bean
  ServiceDependency someDependency() {
    return restServiceDependency("http://example.com/foo").build();
  }
}
