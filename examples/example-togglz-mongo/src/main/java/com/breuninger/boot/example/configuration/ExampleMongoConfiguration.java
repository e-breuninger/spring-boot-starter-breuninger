package com.breuninger.boot.example.configuration;

import static java.util.Arrays.asList;

import static com.breuninger.boot.status.domain.Criticality.FUNCTIONAL_CRITICAL;
import static com.breuninger.boot.status.domain.Criticality.criticality;
import static com.breuninger.boot.status.domain.DatasourceDependencyBuilder.mongoDependency;
import static com.breuninger.boot.status.domain.Expectations.highExpectations;
import static com.breuninger.boot.status.domain.Level.HIGH;
import static com.breuninger.boot.status.domain.Level.LOW;
import static com.breuninger.boot.status.domain.ServiceDependency.AUTH_HMAC;
import static com.breuninger.boot.status.domain.ServiceDependencyBuilder.restServiceDependency;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.breuninger.boot.mongo.configuration.MongoProperties;
import com.breuninger.boot.status.domain.Criticality;
import com.breuninger.boot.status.domain.DatasourceDependency;
import com.breuninger.boot.status.domain.ServiceDependency;

@Configuration
public class ExampleMongoConfiguration {

  @Bean
  public Criticality serviceCriticality() {
    return criticality(LOW, "This is only a test, so the disaster impact should be quite low.");
  }

  @Bean
  public DatasourceDependency togglzMongoDependency(final MongoProperties mongoProperties) {
    return mongoDependency(mongoProperties.toDatasources()).withName("Togglz DB")
      .withDescription("Database used to store the state of the toggles")
      .withCriticality(criticality(HIGH, "Unable to use Togglz"))
      .withExpectations(highExpectations())
      .build();
  }

  @Bean
  public ServiceDependency breuningerDependency() {
    return restServiceDependency("http://api.breuninger.com").withAuthentication(AUTH_HMAC)
      .withMethods(asList("GET", "HEAD"))
      .withMediaTypes(asList("application/json", "application/hal+json"))
      .withName("Breuninger API")
      .withDescription("Just an example to show how to configure a dependency to a REST service.")
      .withExpectations(highExpectations())
      .withCriticality(FUNCTIONAL_CRITICAL)
      .build();
  }
}
