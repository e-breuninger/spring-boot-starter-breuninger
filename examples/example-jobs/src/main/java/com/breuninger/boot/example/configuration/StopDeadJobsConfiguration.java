package com.breuninger.boot.example.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.breuninger.boot.jobs.repository.cleanup.StopDeadJobs;
import com.breuninger.boot.jobs.service.JobService;

@Configuration
public class StopDeadJobsConfiguration {

  private final JobService jobService;

  @Autowired
  public StopDeadJobsConfiguration(final JobService jobService) {
    this.jobService = jobService;
  }

  @Bean
  public StopDeadJobs stopDeadJobsStrategy() {
    return new StopDeadJobs(jobService, 60);
  }
}
