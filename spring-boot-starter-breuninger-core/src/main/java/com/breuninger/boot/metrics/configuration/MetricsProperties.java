package com.breuninger.boot.metrics.configuration;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "breuninger.metrics")
@Validated
public class MetricsProperties {

  private @Valid Graphite graphite;
  private @Valid Slf4j slf4j;

  @Getter
  @Setter
  public static class Slf4j {

    private @NotEmpty String logger;
    private @Min(1) long period = 5;
  }

  @Getter
  @Setter
  public static class Graphite {

    private @NotEmpty String host;
    private @Min(1) int port;
    private @NotEmpty String prefix;
    private boolean addHostToPrefix = true;
  }
}
