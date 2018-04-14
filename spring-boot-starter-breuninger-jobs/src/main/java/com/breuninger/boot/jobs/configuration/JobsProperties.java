package com.breuninger.boot.jobs.configuration;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "breuninger.jobs")
@Validated
public class JobsProperties {

  private boolean externalTrigger = true;
  private @Min(1) int threadCount = 10;
  private @Valid Cleanup cleanup = new Cleanup();
  private @Valid Status status = new Status();

  @Getter
  @Setter
  public static class Cleanup {

    private @Min(1) int numberOfJobsToKeep = 100;
    private @Min(1) int numberOfSkippedJobsToKeep = 10;
    private @Min(1) int markDeadAfter = 30;
  }

  @Getter
  @Setter
  public static class Status {

    private boolean enabled = true;
    private @NotNull Map<String, String> calculator = new HashMap<>();

    public void setCalculator(final Map<String, String> calculator) {
      final Map<String, String> normalized = new HashMap<>();
      calculator.forEach((key, value) -> normalized.put(key.toLowerCase().replace(" ", "-"), value));
      this.calculator = normalized;
    }
  }
}
