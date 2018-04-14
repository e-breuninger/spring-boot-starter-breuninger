package com.breuninger.boot.status.indicator;

import com.breuninger.boot.status.domain.ApplicationStatus;

public interface ApplicationStatusAggregator {

  ApplicationStatus aggregatedStatus();

  default void update() {
  }
}
