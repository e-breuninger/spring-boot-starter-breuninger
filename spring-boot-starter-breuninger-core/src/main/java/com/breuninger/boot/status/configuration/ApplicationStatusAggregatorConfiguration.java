package com.breuninger.boot.status.configuration;

import static java.util.Collections.emptyList;

import static com.breuninger.boot.status.domain.ApplicationStatus.applicationStatus;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.breuninger.boot.status.domain.ApplicationInfo;
import com.breuninger.boot.status.domain.ClusterInfo;
import com.breuninger.boot.status.domain.SystemInfo;
import com.breuninger.boot.status.domain.TeamInfo;
import com.breuninger.boot.status.domain.VersionInfo;
import com.breuninger.boot.status.indicator.ApplicationStatusAggregator;
import com.breuninger.boot.status.indicator.CachedApplicationStatusAggregator;
import com.breuninger.boot.status.indicator.StatusDetailIndicator;
import com.breuninger.boot.status.scheduler.CronScheduler;
import com.breuninger.boot.status.scheduler.EveryTenSecondsScheduler;
import com.breuninger.boot.status.scheduler.Scheduler;

@Configuration
@EnableScheduling
public class ApplicationStatusAggregatorConfiguration {

  private final List<StatusDetailIndicator> statusDetailIndicators;
  private final ClusterInfo clusterInfo;

  public ApplicationStatusAggregatorConfiguration(
    @Autowired(required = false) final List<StatusDetailIndicator> statusDetailIndicators,
    @Autowired(required = false) final ClusterInfo clusterInfo) {
    if (statusDetailIndicators == null) {
      this.statusDetailIndicators = emptyList();
    } else {
      this.statusDetailIndicators = statusDetailIndicators;
    }
    this.clusterInfo = clusterInfo;
  }

  @Bean
  @ConditionalOnMissingBean(ApplicationStatusAggregator.class)
  public ApplicationStatusAggregator applicationStatusAggregator(final ApplicationInfo applicationInfo,
                                                                 final VersionInfo versionInfo, final SystemInfo systemInfo,
                                                                 final TeamInfo teamInfo) {
    return new CachedApplicationStatusAggregator(
      applicationStatus(applicationInfo, clusterInfo, systemInfo, versionInfo, teamInfo, emptyList()), statusDetailIndicators);
  }

  @Bean
  @ConditionalOnProperty(name = "breuninger.status.scheduler.cron")
  public Scheduler cronScheduler(final ApplicationStatusAggregator applicationStatusAggregator) {
    return new CronScheduler(applicationStatusAggregator);
  }

  @Bean
  @ConditionalOnMissingBean(Scheduler.class)
  public Scheduler fixedDelayScheduler(final ApplicationStatusAggregator applicationStatusAggregator) {
    return new EveryTenSecondsScheduler(applicationStatusAggregator);
  }
}
