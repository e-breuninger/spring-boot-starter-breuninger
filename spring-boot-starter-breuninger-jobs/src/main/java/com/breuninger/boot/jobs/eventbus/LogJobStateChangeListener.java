package com.breuninger.boot.jobs.eventbus;

import com.breuninger.boot.jobs.eventbus.events.StateChangeEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogJobStateChangeListener implements JobStateChangeListener {

  @Override
  public void consumeStateChange(final StateChangeEvent stateChangeEvent) {
    LOG.info("jobType='{}' state changed to '{}' ('{}'): {}", stateChangeEvent.getJobType(), stateChangeEvent.getState(),
      stateChangeEvent.getJobId(), stateChangeEvent.getMessage());
  }
}
