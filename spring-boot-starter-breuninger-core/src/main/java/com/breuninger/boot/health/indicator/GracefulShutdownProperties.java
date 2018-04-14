package com.breuninger.boot.health.indicator;

import javax.validation.constraints.Min;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "breuninger.gracefulshutdown")
@Validated
public class GracefulShutdownProperties {

  private boolean enabled;
  private @Min(0) long indicateErrorAfter = 5000L;
  private @Min(100) long phaseOutAfter = 20000L;
}
