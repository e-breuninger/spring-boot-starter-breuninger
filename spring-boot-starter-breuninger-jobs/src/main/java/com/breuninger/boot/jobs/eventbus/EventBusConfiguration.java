package com.breuninger.boot.jobs.eventbus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.breuninger.boot.jobs.service.JobService;

@Configuration
public class EventBusConfiguration {

  private final JobService jobService;

  public EventBusConfiguration(final JobService jobService) {
    this.jobService = jobService;
  }

  @Bean
  public JobStateChangeListener logJobEventListener() {
    return new LogJobStateChangeListener();
  }

  @Bean
  public JobStateChangeListener persistenceJobEventListener() {
    return new PersistenceJobStateChangeListener(jobService);
  }
}
