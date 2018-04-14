package com.breuninger.boot.jobs.service;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;

import static com.breuninger.boot.jobs.eventbus.events.StateChangeEvent.State.KEEP_ALIVE;
import static com.breuninger.boot.jobs.eventbus.events.StateChangeEvent.State.RESTART;
import static com.breuninger.boot.jobs.eventbus.events.StateChangeEvent.State.SKIPPED;
import static com.breuninger.boot.jobs.eventbus.events.StateChangeEvent.State.START;
import static com.breuninger.boot.jobs.eventbus.events.StateChangeEvent.State.STOP;
import static com.breuninger.boot.jobs.eventbus.events.StateChangeEvent.newStateChangeEvent;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.MDC;
import org.slf4j.Marker;
import org.springframework.context.ApplicationEventPublisher;

import com.breuninger.boot.jobs.domain.JobMarker;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class JobRunner implements Runnable {

  private static final long PING_PERIOD = 20L;

  private final String jobId;
  private final JobRunnable jobRunnable;
  private final ScheduledExecutorService executorService;
  private final ApplicationEventPublisher eventPublisher;
  private final Marker jobMarker;
  private ScheduledFuture<?> pingJob;

  private JobRunner(final String jobId, final JobRunnable jobRunnable, final ApplicationEventPublisher eventPublisher,
                    final ScheduledExecutorService executorService) {
    this.jobId = jobId;
    this.jobRunnable = jobRunnable;
    this.eventPublisher = eventPublisher;
    this.executorService = executorService;
    jobMarker = JobMarker.jobMarker(jobRunnable.getJobDefinition().jobType());
  }

  public static JobRunner newJobRunner(final String jobId, final JobRunnable jobRunnable,
                                       final ApplicationEventPublisher eventPublisher,
                                       final ScheduledExecutorService executorService) {
    return new JobRunner(jobId, jobRunnable, eventPublisher, executorService);
  }

  @Override
  public void run() {
    start();
    try {
      final var jobDefinition = jobRunnable.getJobDefinition();
      final var restarts = jobDefinition.restarts();
      final var retryDelay = jobDefinition.retryDelay();
      executeAndRetry(restarts, retryDelay);
    } catch (final RuntimeException e) {
      error(e);
    } finally {
      stop();
    }
  }

  private synchronized void executeAndRetry(final int restarts, final Optional<Duration> retryDelay) {
    try {
      final var executed = jobRunnable.execute();
      if (!executed) {
        eventPublisher.publishEvent(newStateChangeEvent(jobRunnable, jobId, SKIPPED));
      }
    } catch (final RuntimeException e) {
      if (restarts > 0) {
        LOG.warn("Restarting job because of an exception caught during execution: " + e.getMessage());
        eventPublisher.publishEvent(newStateChangeEvent(jobRunnable, jobId, RESTART));
        retryDelay.ifPresent(this::sleep);
        executeAndRetry(restarts - 1, retryDelay);
      } else {
        error(e);
      }
    }
  }

  private void sleep(final Duration duration) {
    try {
      Thread.sleep(duration.toMillis());
    } catch (final InterruptedException e) {
      LOG.error(jobMarker, "InterruptedException", e);
    }
  }

  synchronized void start() {
    MDC.put("job_id", jobId.substring(jobId.lastIndexOf('/') + 1));
    MDC.put("job_type", jobRunnable.getJobDefinition().jobType());
    eventPublisher.publishEvent(newStateChangeEvent(jobRunnable, jobId, START));
    pingJob = executorService.scheduleAtFixedRate(this::ping, PING_PERIOD, PING_PERIOD, SECONDS);
    LOG.info(jobMarker, "Job started '{}'", jobId);
  }

  void ping() {
    try {
      eventPublisher.publishEvent(newStateChangeEvent(jobRunnable, jobId, KEEP_ALIVE));
    } catch (final Exception e) {
      LOG.error(jobMarker, "Fatal error in ping job for" + jobRunnable.getJobDefinition().jobType() + " (" + jobId + ")", e);
    }
  }

  synchronized void error(final Exception e) {
    LOG.error(jobMarker, format("Fatal error in job %s (%s)", jobRunnable.getJobDefinition().jobType(), jobId), e);
  }

  synchronized void stop() {
    pingJob.cancel(false);

    try {
      eventPublisher.publishEvent(newStateChangeEvent(jobRunnable, jobId, STOP));
      LOG.info(jobMarker, "Job stopped '{}'", jobId);
    } finally {
      MDC.clear();
    }
  }
}
