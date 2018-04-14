package com.breuninger.boot.jobs.definition;

import java.time.Duration;
import java.util.Optional;

public interface JobDefinition {

  String jobType();

  String jobName();

  String description();

  default Optional<Duration> maxAge() {
    return Optional.empty();
  }

  default Optional<Duration> fixedDelay() {
    return Optional.empty();
  }

  default Optional<String> cron() {
    return Optional.empty();
  }

  default int restarts() {
    return 0;
  }

  default int retries() {
    return 0;
  }

  default Optional<Duration> retryDelay() {
    return Optional.empty();
  }
}
