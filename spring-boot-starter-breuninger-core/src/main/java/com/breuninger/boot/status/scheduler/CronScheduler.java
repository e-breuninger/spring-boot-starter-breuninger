package com.breuninger.boot.status.scheduler;

import org.springframework.scheduling.annotation.Scheduled;

import com.breuninger.boot.status.indicator.ApplicationStatusAggregator;

public final class CronScheduler implements Scheduler {

  private final ApplicationStatusAggregator aggregator;

  public CronScheduler(final ApplicationStatusAggregator aggregator) {
    this.aggregator = aggregator;
  }

  @Override
  @Scheduled(cron = "${breuninger.status.scheduler.cron}")
  public void update() {
    aggregator.update();
  }
}
