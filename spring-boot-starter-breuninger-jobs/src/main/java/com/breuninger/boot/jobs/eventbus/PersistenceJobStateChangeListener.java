package com.breuninger.boot.jobs.eventbus;

import static java.time.Instant.ofEpochMilli;
import static java.time.OffsetDateTime.ofInstant;
import static java.time.ZoneId.systemDefault;

import static com.breuninger.boot.jobs.domain.JobMessage.jobMessage;
import static com.breuninger.boot.jobs.domain.Level.ERROR;

import com.breuninger.boot.jobs.eventbus.events.StateChangeEvent;
import com.breuninger.boot.jobs.service.JobService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersistenceJobStateChangeListener implements JobStateChangeListener {

  private final JobService jobService;

  public PersistenceJobStateChangeListener(final JobService jobService) {
    this.jobService = jobService;
  }

  @Override
  public void consumeStateChange(final StateChangeEvent event) {
    try {
      switch (event.getState()) {
        case START:
          break;
        case KEEP_ALIVE:
          jobService.keepAlive(event.getJobId());
          break;
        case FAILED:
          final var ts = ofInstant(ofEpochMilli(event.getTimestamp()), systemDefault());
          jobService.appendMessage(event.getJobId(), jobMessage(ERROR, event.getMessage(), ts));
          break;
        case RESTART:
          jobService.markRestarted(event.getJobId());
          break;
        case DEAD:
          jobService.killJob(event.getJobId());
          break;
        case SKIPPED:
          jobService.markSkipped(event.getJobId());
          jobService.stopJob(event.getJobId());
          break;
        case STOP:
          jobService.stopJob(event.getJobId());
          break;
      }
    } catch (final RuntimeException e) {
      LOG.error("Failed to persist job state change: jobId=" + event.getJobId() + ", state=" + event.getState() + ", message=" +
        event.getMessage(), e);
    }
  }
}
