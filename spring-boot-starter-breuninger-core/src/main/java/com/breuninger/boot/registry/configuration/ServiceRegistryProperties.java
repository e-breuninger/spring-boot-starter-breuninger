package com.breuninger.boot.registry.configuration;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@ConfigurationProperties(prefix = "breuninger.serviceregistry")
@Validated
public class ServiceRegistryProperties {

  private boolean enabled = true;
  private String servers;
  private @URL String service;
  private @Min(1) long expireAfter = 15L;
  private @Min(1) long refreshAfter = 5L;
}
