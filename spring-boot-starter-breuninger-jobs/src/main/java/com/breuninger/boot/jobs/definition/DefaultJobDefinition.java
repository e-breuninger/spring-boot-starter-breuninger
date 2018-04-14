package com.breuninger.boot.jobs.definition;

import java.time.Duration;
import java.util.Optional;

import org.springframework.scheduling.support.CronSequenceGenerator;

public final class DefaultJobDefinition implements JobDefinition {

  private final String jobType;
  private final String jobName;
  private final String description;
  private final Optional<Duration> maxAge;
  private final Optional<Duration> fixedDelay;
  private final Optional<String> cron;
  private final int restarts;
  private final int retries;
  private final Optional<Duration> retryDelay;

  private DefaultJobDefinition(final String jobType, final String jobName, final String description,
                               final Optional<Duration> maxAge, final Optional<Duration> fixedDelay, final Optional<String> cron,
                               final int restarts, final int retries, final Optional<Duration> retryDelay) {
    this.jobType = jobType;
    this.jobName = jobName;
    this.description = description;
    this.maxAge = maxAge;
    this.fixedDelay = fixedDelay;
    this.cron = cron;
    this.restarts = restarts;
    this.retries = retries;
    this.retryDelay = retryDelay;
    cron.ifPresent(DefaultJobDefinition::validateCron);
  }

  public static JobDefinition manuallyTriggerableJobDefinition(final String jobType, final String jobName,
                                                               final String description, final int restarts,
                                                               final Optional<Duration> maxAge) {
    return new DefaultJobDefinition(jobType, jobName, description, maxAge, Optional.empty(), Optional.empty(), restarts, 0,
      Optional.empty());
  }

  public static JobDefinition cronJobDefinition(final String jobType, final String jobName, final String description,
                                                final String cron, final int restarts, final Optional<Duration> maxAge) {
    return new DefaultJobDefinition(jobType, jobName, description, maxAge, Optional.empty(), Optional.of(cron), restarts, 0,
      Optional.empty());
  }

  public static JobDefinition retryableCronJobDefinition(final String jobType, final String jobName, final String description,
                                                         final String cron, final int restarts, final int retries,
                                                         final Duration retryDelay, final Optional<Duration> maxAge) {
    return new DefaultJobDefinition(jobType, jobName, description, maxAge, Optional.empty(), Optional.of(cron), restarts, retries,
      Optional.of(retryDelay));
  }

  public static DefaultJobDefinition fixedDelayJobDefinition(final String jobType, final String jobName, final String description,
                                                             final Duration fixedDelay, final int restarts,
                                                             final Optional<Duration> maxAge) {
    return new DefaultJobDefinition(jobType, jobName, description, maxAge, Optional.of(fixedDelay), Optional.empty(), restarts, 0,
      Optional.empty());
  }

  public static DefaultJobDefinition retryableFixedDelayJobDefinition(final String jobType, final String jobName,
                                                                      final String description, final Duration fixedDelay,
                                                                      final int restarts, final int retries,
                                                                      final Optional<Duration> retryDelay,
                                                                      final Optional<Duration> maxAge) {
    return new DefaultJobDefinition(jobType, jobName, description, maxAge, Optional.of(fixedDelay), Optional.empty(), restarts,
      retries, retryDelay);
  }

  public static void validateCron(final String cron) {
    new CronSequenceGenerator(cron);
  }

  @Override
  public String jobType() {
    return jobType;
  }

  @Override
  public String jobName() {
    return jobName;
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public Optional<Duration> maxAge() {
    return maxAge;
  }

  @Override
  public Optional<Duration> fixedDelay() {
    return fixedDelay;
  }

  @Override
  public Optional<String> cron() {
    return cron;
  }

  @Override
  public int restarts() {
    return restarts;
  }

  @Override
  public int retries() {
    return retries;
  }

  @Override
  public Optional<Duration> retryDelay() {
    return retryDelay;
  }
}
