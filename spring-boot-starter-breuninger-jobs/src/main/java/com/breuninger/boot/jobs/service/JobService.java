package com.breuninger.boot.jobs.service;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.time.OffsetDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNullElseGet;

import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus;
import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus.DEAD;
import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus.ERROR;
import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus.OK;
import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus.SKIPPED;
import static com.breuninger.boot.jobs.domain.JobInfo.newJobInfo;
import static com.breuninger.boot.jobs.domain.JobMessage.jobMessage;
import static com.breuninger.boot.jobs.domain.Level.INFO;
import static com.breuninger.boot.jobs.domain.Level.WARNING;
import static com.breuninger.boot.jobs.service.JobRunner.newJobRunner;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.breuninger.boot.jobs.definition.JobDefinition;
import com.breuninger.boot.jobs.domain.JobInfo;
import com.breuninger.boot.jobs.domain.JobMessage;
import com.breuninger.boot.jobs.domain.Level;
import com.breuninger.boot.jobs.repository.JobBlockedException;
import com.breuninger.boot.jobs.repository.JobRepository;
import com.breuninger.boot.status.domain.SystemInfo;

import lombok.extern.slf4j.Slf4j;

import io.micrometer.core.instrument.Metrics;

@Slf4j
@Service
public class JobService {

  private final JobRepository jobRepository;
  private final JobMetaService jobMetaService;
  private final List<JobRunnable> jobRunnables;
  private final ScheduledExecutorService executor;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final Clock clock;
  private final SystemInfo systemInfo;
  private final UuidProvider uuidProvider;

  JobService(final JobRepository jobRepository, final JobMetaService jobMetaService,
             @Autowired(required = false) final List<JobRunnable> jobRunnables, final ScheduledExecutorService executor,
             final ApplicationEventPublisher applicationEventPublisher, @Autowired(required = false) final Clock clock,
             final SystemInfo systemInfo, final UuidProvider uuidProvider) {
    this.jobRepository = jobRepository;
    this.jobMetaService = jobMetaService;
    if (jobRunnables == null) {
      this.jobRunnables = emptyList();
    } else {
      this.jobRunnables = jobRunnables;
    }
    this.executor = executor;
    this.applicationEventPublisher = applicationEventPublisher;
    this.clock = requireNonNullElseGet(clock, Clock::systemDefaultZone);
    this.systemInfo = systemInfo;
    this.uuidProvider = uuidProvider;
  }

  @PostConstruct
  public void postConstruct() {
    LOG.info("Found {} JobRunnables: {}", jobRunnables.size(),
      jobRunnables.stream().map(j -> j.getJobDefinition().jobType()).collect(Collectors.toList()));
  }

  public Optional<String> startAsyncJob(final String jobType) {
    try {
      final var jobRunnable = findJobRunnable(jobType);
      final var jobInfo = createJobInfo(jobType);
      jobMetaService.aquireRunLock(jobInfo.getJobId(), jobInfo.getJobType());
      jobRepository.createOrUpdate(jobInfo);
      return Optional.of(startAsync(metered(jobRunnable), jobInfo.getJobId()));
    } catch (final JobBlockedException e) {
      LOG.info(e.getMessage());
      return Optional.empty();
    }
  }

  public Optional<JobInfo> findJob(final String id) {
    return jobRepository.findOne(id);
  }

  public List<JobInfo> findJobs(final Optional<String> type, final int count) {
    if (type.isPresent()) {
      return jobRepository.findLatestBy(type.get(), count);
    } else {
      return jobRepository.findLatest(count);
    }
  }

  public List<JobInfo> findJobsDistinct() {
    return jobRepository.findLatestJobsDistinct();
  }

  public void deleteJobs(final Optional<String> type) {
    if (type.isPresent()) {
      jobRepository.findByType(type.get()).forEach(j -> jobRepository.removeIfStopped(j.getJobId()));
    } else {
      jobRepository.findAll().forEach(j -> jobRepository.removeIfStopped(j.getJobId()));
    }
  }

  public void stopJob(final String jobId) {
    stopJob(jobId, null);
  }

  public void killJobsDeadSince(final int seconds) {
    final var timeToMarkJobAsStopped = now(clock).minusSeconds(seconds);
    LOG.info(format("JobCleanup: Looking for jobs older than %s ", timeToMarkJobAsStopped));
    final var deadJobs = jobRepository.findRunningWithoutUpdateSince(timeToMarkJobAsStopped);
    deadJobs.forEach(deadJob -> killJob(deadJob.getJobId()));
    clearRunLocks();
  }

  // TODO: This method should never do something, otherwise the is a bug in the lock handling.
  // TODO: Check Log files + Remove
  private void clearRunLocks() {
    jobMetaService.runningJobs().forEach(runningJob -> {
      final var jobInfoOptional = jobRepository.findOne(runningJob.jobId);
      if (jobInfoOptional.isPresent() && jobInfoOptional.get().isStopped()) {
        jobMetaService.releaseRunLock(runningJob.jobType);
        LOG.error("Clear Lock of Job {}. Job stopped already.", runningJob.jobType);
      } else if (!jobInfoOptional.isPresent()) {
        jobMetaService.releaseRunLock(runningJob.jobType);
        LOG.error("Clear Lock of Job {}. JobID does not exist", runningJob.jobType);
      }
    });
  }

  public void killJob(final String jobId) {
    stopJob(jobId, DEAD);
    jobRepository.appendMessage(jobId,
      jobMessage(WARNING, "Job didn't receive updates for a while, considering it dead", now(clock)));
  }

  private void stopJob(final String jobId, final JobStatus status) {
    jobRepository.findOne(jobId).ifPresent(jobInfo -> {
      jobMetaService.releaseRunLock(jobInfo.getJobType());
      final var now = now(clock);
      final var builder = jobInfo.copy().setStopped(now).setLastUpdated(now);
      if (status != null) {
        builder.setStatus(status);
      }
      jobRepository.createOrUpdate(builder.build());
    });
  }

  public void appendMessage(final String jobId, final JobMessage jobMessage) {
    writeMessageAndStatus(jobId, jobMessage.getLevel(), jobMessage.getMessage(),
      jobMessage.getLevel() == Level.ERROR ? ERROR : OK, jobMessage.getTimestamp());
  }

  public void keepAlive(final String jobId) {
    jobRepository.setLastUpdate(jobId, now(clock));
  }

  public void markSkipped(final String jobId) {
    writeMessageAndStatus(jobId, INFO, "Skipped job ..", SKIPPED);
  }

  public void markRestarted(final String jobId) {
    writeMessageAndStatus(jobId, WARNING, "Restarting job ..", OK);
  }

  private void writeMessageAndStatus(final String jobId, final Level messageLevel, final String message, final JobStatus jobStatus) {
    final var currentTimestamp = now(clock);
    writeMessageAndStatus(jobId, messageLevel, message, jobStatus, currentTimestamp);
  }

  private void writeMessageAndStatus(final String jobId, final Level messageLevel, final String message, final JobStatus jobStatus,
                                     final OffsetDateTime timestamp) {
    // TODO: Refactor JobRepository so only a single update is required
    jobRepository.appendMessage(jobId, jobMessage(messageLevel, message, timestamp));
    jobRepository.setJobStatus(jobId, jobStatus);
  }

  private JobInfo createJobInfo(final String jobType) {
    return newJobInfo(uuidProvider.getUuid(), jobType, clock, systemInfo.getHostname());
  }

  private JobRunnable findJobRunnable(final String jobType) {
    final var optionalRunnable = jobRunnables.stream()
      .filter(r -> r.getJobDefinition().jobType().equalsIgnoreCase(jobType))
      .findFirst();
    return optionalRunnable.orElseThrow(() -> new IllegalArgumentException("No JobRunnable for " + jobType));
  }

  private String startAsync(final JobRunnable jobRunnable, final String jobId) {
    executor.execute(newJobRunner(jobId, jobRunnable, applicationEventPublisher, executor));
    return jobId;
  }

  private JobRunnable metered(final JobRunnable delegate) {
    return new JobRunnable() {

      @Override
      public JobDefinition getJobDefinition() {
        return delegate.getJobDefinition();
      }

      @Override
      public boolean execute() {
        final var ts = currentTimeMillis();
        final var executed = delegate.execute();
        Metrics.gauge(gaugeName(), (currentTimeMillis() - ts) / 1000L);
        return executed;
      }

      private String gaugeName() {
        return "gauge.jobs.runtime." + delegate.getJobDefinition().jobType().toLowerCase();
      }
    };
  }
}
