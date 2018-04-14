package com.breuninger.boot.jobs.eventbus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.breuninger.boot.jobs.repository.JobRepository;
import com.breuninger.boot.jobs.repository.inmem.InMemJobRepository;

@Configuration
public class EventBusTestConfiguration {

  @Bean
  public InMemoryEventRubbishBin testInMemoryEventListener() {
    return new InMemoryEventRubbishBin();
  }

  @Bean
  public JobRepository jobRepository() {
    return new InMemJobRepository();
  }
}
