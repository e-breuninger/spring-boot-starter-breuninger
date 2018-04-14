package com.breuninger.boot.registry.configuration;

import static com.breuninger.boot.status.domain.Expectations.expects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.breuninger.boot.status.domain.Availability;
import com.breuninger.boot.status.domain.Criticality;
import com.breuninger.boot.status.domain.Level;
import com.breuninger.boot.status.domain.Performance;
import com.breuninger.boot.status.domain.ServiceDependency;
import com.breuninger.boot.status.domain.ServiceDependencyBuilder;

@Configuration
@EnableConfigurationProperties(ServiceRegistryProperties.class)
public class ServiceRegistryConfiguration {

  private final ServiceRegistryProperties serviceRegistryProperties;

  public ServiceRegistryConfiguration(final ServiceRegistryProperties serviceRegistryProperties) {
    this.serviceRegistryProperties = serviceRegistryProperties;
  }

  @Bean
  @ConditionalOnProperty(prefix = "breuninger.serviceregistry", name = "enabled", havingValue = "true")
  public ServiceDependency serviceRegistryDependency() {
    return ServiceDependencyBuilder.serviceDependency(serviceRegistryProperties.getServers())
      .withName("Service Registry")
      .withDescription("Registers this service at a service registry")
      .withExpectations(expects(Availability.MEDIUM, Performance.MEDIUM))
      .withCriticality(Criticality.criticality(Level.HIGH, "Service cannot be registered"))
      .build();
  }
}

