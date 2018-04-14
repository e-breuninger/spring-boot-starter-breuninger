package com.breuninger.boot.jobs.repository.cleanup;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.groupingBy;

import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus.SKIPPED;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.scheduling.annotation.Scheduled;

import com.breuninger.boot.jobs.domain.JobInfo;
import com.breuninger.boot.jobs.repository.JobRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeleteSkippedJobs implements JobCleanupStrategy {

  private static final long KEEP_LAST_JOBS_CLEANUP_INTERVAL = 10L * 60L * 1000L;

  private final int numberOfJobsToKeep;
  private final JobRepository jobRepository;

  public DeleteSkippedJobs(final JobRepository jobRepository, final int numberOfJobsToKeep) {
    this.jobRepository = jobRepository;
    this.numberOfJobsToKeep = numberOfJobsToKeep;
    LOG.info("DeleteSkippedJobs strategy configured with numberOfJobsToKeep='{}'", numberOfJobsToKeep);
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
      .forEach((jobType, jobExecutions) -> jobExecutions.stream()
        .filter(j -> j.isStopped() && Objects.equals(j.getStatus(), SKIPPED))
        .skip(numberOfJobsToKeep)
        .forEach(jobsToDelete::add));
    return jobsToDelete;
  }
}
