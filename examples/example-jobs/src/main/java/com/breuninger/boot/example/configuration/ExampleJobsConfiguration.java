package com.breuninger.boot.example.configuration;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig.Builder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.breuninger.boot.jobs.repository.JobRepository;
import com.breuninger.boot.jobs.repository.cleanup.KeepLastJobs;
import com.breuninger.boot.jobs.service.JobMutexGroup;

@Configuration
public class ExampleJobsConfiguration {

  @Bean
  public AsyncHttpClient httpClient() {
    return new DefaultAsyncHttpClient(new Builder().build());
  }

  @Bean
  public KeepLastJobs keepLast10FooJobsCleanupStrategy(final JobRepository jobRepository) {
    return new KeepLastJobs(jobRepository, 10);
  }

  @Bean
  public JobMutexGroup mutualExclusion() {
    return new JobMutexGroup("barFizzle", "Bar", "Fizzle");
  }
}
