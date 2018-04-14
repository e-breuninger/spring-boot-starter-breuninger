package com.breuninger.boot.jobs.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TestConfiguration {

  @Bean
  JobMutexGroup fooGroup() {
    return new JobMutexGroup("Foo", "A", "B");
  }

  @Bean
  JobMutexGroup barGroup() {
    return new JobMutexGroup("Bar", "B", "C");
  }
}
