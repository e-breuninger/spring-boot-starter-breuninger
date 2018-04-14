package com.breuninger.boot.jobs.repository.cleanup;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.groupingBy;

import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus.OK;

import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;

import com.breuninger.boot.jobs.domain.JobInfo;
import com.breuninger.boot.jobs.repository.JobRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeepLastJobs implements JobCleanupStrategy {

  private static final long KEEP_LAST_JOBS_CLEANUP_INTERVAL = 10L * 60L * 1000L;

  private final int numberOfJobsToKeep;
  private final JobRepository jobRepository;

  public KeepLastJobs(final JobRepository jobRepository, final int numberOfJobsToKeep) {
    this.jobRepository = jobRepository;
    this.numberOfJobsToKeep = numberOfJobsToKeep;
    LOG.info("KeepLastJobs strategy configured with numberOfJobsToKeep='{}'", numberOfJobsToKeep);
  }

  @Override
  @Scheduled(fixedRate = KEEP_LAST_JOBS_CLEANUP_INTERVAL)
  public void doCleanUp() {
    final var jobs = jobRepository.findAllJobInfoWithoutMessages();

    findJobsToDelete(jobs).forEach(jobInfo -> jobRepository.removeIfStopped(jobInfo.getJobId()));
  }

  private List<JobInfo> findJobsToDelete(final List<JobInfo> jobs) {
    final List<JobInfo> jobsToDelete = new ArrayList<>();
    jobs.stream()
      .sorted(comparing(JobInfo::getStarted, reverseOrder()))
      .collect(groupingBy(JobInfo::getJobType))
      .forEach((jobType, jobExecutions) -> {
        final var lastOkExecution = jobExecutions.stream()
          .filter(j -> j.isStopped() && j.getStatus() == OK)
          .findFirst();
        jobExecutions.stream()
          .filter(JobInfo::isStopped)
          .skip(numberOfJobsToKeep)
          .filter(j -> !(lastOkExecution.isPresent() && lastOkExecution.get().equals(j)))
          .forEach(jobsToDelete::add);
      });
    return jobsToDelete;
  }
}
