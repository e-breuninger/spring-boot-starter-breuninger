package com.breuninger.boot.status.indicator;

import static java.util.stream.Collectors.toList;

import static com.breuninger.boot.status.domain.ApplicationStatus.applicationStatus;

import java.util.List;

import com.breuninger.boot.status.domain.ApplicationStatus;

public class CachedApplicationStatusAggregator implements ApplicationStatusAggregator {

  private final List<StatusDetailIndicator> indicators;
  private volatile ApplicationStatus cachedStatus;

  public CachedApplicationStatusAggregator(final ApplicationStatus applicationStatus,
                                           final List<StatusDetailIndicator> indicators) {
    cachedStatus = applicationStatus;
    this.indicators = indicators;
  }

  @Override
  public ApplicationStatus aggregatedStatus() {
    return cachedStatus;
  }

  @Override
  public void update() {
    cachedStatus = applicationStatus(cachedStatus.application, cachedStatus.cluster, cachedStatus.system, cachedStatus.vcs,
      cachedStatus.team, indicators.stream().flatMap(i -> i.statusDetails().stream()).collect(toList()));
  }
}
