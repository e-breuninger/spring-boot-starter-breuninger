package com.breuninger.boot.example.status;

import static com.breuninger.boot.status.domain.Criticality.mediumCriticality;
import static com.breuninger.boot.status.domain.Expectations.lowExpectations;
import static com.breuninger.boot.status.domain.ServiceDependencyBuilder.restServiceDependency;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.breuninger.boot.status.domain.ExternalDependency;

@Configuration
public class DependenciesConfiguration {

  @Bean
  ExternalDependency fooClient() {
    return restServiceDependency("http://example.org/api/foo").withName("Foo Service").build();
  }

  @Bean
  ExternalDependency barClient() {
    return restServiceDependency("http://example.org/api/bar").withName("Bar Service")
      .withCriticality(mediumCriticality("Data will become inconsistent"))
      .withExpectations(lowExpectations())
      .build();
  }
}
