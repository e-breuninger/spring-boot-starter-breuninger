package com.breuninger.boot.status.scheduler;

import org.springframework.scheduling.annotation.Scheduled;

import com.breuninger.boot.status.indicator.ApplicationStatusAggregator;

public final class EveryTenSecondsScheduler implements Scheduler {

  private static final int TEN_SECONDS = 10 * 1000;

  private final ApplicationStatusAggregator aggregator;

  public EveryTenSecondsScheduler(final ApplicationStatusAggregator aggregator) {
    this.aggregator = aggregator;
  }

  @Override
  @Scheduled(fixedDelay = TEN_SECONDS)
  public void update() {
    aggregator.update();
  }
}
