package com.breuninger.boot.jobs.repository.cleanup;

import org.springframework.scheduling.annotation.Scheduled;

import com.breuninger.boot.jobs.service.JobService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StopDeadJobs implements JobCleanupStrategy {

  private static final long STOP_DEAD_JOBS_CLEANUP_INTERVAL = 60L * 1000L;

  private final int stopJobAfterSeconds;
  private final JobService jobService;

  public StopDeadJobs(final JobService jobService, final int stopJobAfterSeconds) {
    this.jobService = jobService;
    this.stopJobAfterSeconds = stopJobAfterSeconds;
    LOG.info("Mark old as stopped after '{}' seconds of inactivity", stopJobAfterSeconds);
  }

  @Override
  @Scheduled(fixedRate = STOP_DEAD_JOBS_CLEANUP_INTERVAL)
  public void doCleanUp() {
    jobService.killJobsDeadSince(stopJobAfterSeconds);
  }
}
