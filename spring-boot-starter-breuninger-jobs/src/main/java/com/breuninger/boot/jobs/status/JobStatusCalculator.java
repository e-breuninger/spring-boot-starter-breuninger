package com.breuninger.boot.jobs.status;

import static java.lang.String.format;
import static java.time.OffsetDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.util.Collections.singletonList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.breuninger.boot.jobs.definition.JobDefinition;
import com.breuninger.boot.jobs.domain.JobInfo;
import com.breuninger.boot.jobs.domain.JobInfo.JobStatus;
import com.breuninger.boot.jobs.repository.JobRepository;
import com.breuninger.boot.status.domain.Link;
import com.breuninger.boot.status.domain.Status;
import com.breuninger.boot.status.domain.StatusDetail;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobStatusCalculator {

  private static final String SUCCESS_MESSAGE = "Last job was successful";
  private static final String ERROR_MESSAGE = "Job had an error";
  private static final String DEAD_MESSAGE = "Job died";
  private static final String TOO_MANY_JOBS_FAILED_MESSAGE = "%d out of %d job executions failed";
  private static final String JOB_TOO_OLD_MESSAGE = "Job didn't run in the past %s";
  private static final String LOAD_JOBS_EXCEPTION_MESSAGE = "Failed to load job status";

  private static final String REL_JOB = "http://github.com/e-breuninger/spring-boot-starter-breuninger/link-relations/job";

  private final String key;
  private final int numberOfJobs;
  private final int maxFailedJobs;
  private final JobRepository jobRepository;
  private final String breuningerManagementBasePath;

  public JobStatusCalculator(final String key, final int numberOfJobs, final int maxFailedJobs, final JobRepository jobRepository,
                             final String breuningerManagementBasePath) {
    checkArgument(!key.isEmpty(), "Key must not be empty");
    checkArgument(maxFailedJobs <= numberOfJobs, "Parameter maxFailedJobs must not be greater numberOfJobs");
    checkArgument(numberOfJobs > 0, "Parameter numberOfJobs must be greater 0");
    checkArgument(maxFailedJobs >= 0, "Parameter maxFailedJobs must not be negative");
    this.key = key;
    this.numberOfJobs = numberOfJobs;
    this.maxFailedJobs = maxFailedJobs;
    this.jobRepository = jobRepository;
    this.breuningerManagementBasePath = breuningerManagementBasePath;
  }

  public static JobStatusCalculator warningOnLastJobFailed(final String key, final JobRepository jobRepository,
                                                           final String breuningerManagementBasePath) {
    return new JobStatusCalculator(key, 1, 1, jobRepository, breuningerManagementBasePath);
  }

  public static JobStatusCalculator errorOnLastJobFailed(final String key, final JobRepository jobRepository,
                                                         final String breuningerManagementBasePath) {
    return new JobStatusCalculator(key, 1, 0, jobRepository, breuningerManagementBasePath);
  }

  public static JobStatusCalculator errorOnLastNumJobsFailed(final String key, final int numJobs,
                                                             final JobRepository jobRepository,
                                                             final String breuningerManagementBasePath) {
    return new JobStatusCalculator(key, numJobs, numJobs - 1, jobRepository, breuningerManagementBasePath);
  }

  public String getKey() {
    return key;
  }

  public StatusDetail statusDetail(final JobDefinition jobDefinition) {
    try {
      final var jobs = jobRepository.findLatestBy(jobDefinition.jobType(), numberOfJobs);
      return jobs.isEmpty() ? statusDetailWhenNoJobAvailable(jobDefinition) : toStatusDetail(jobs, jobDefinition);
    } catch (final Exception e) {
      LOG.error(LOAD_JOBS_EXCEPTION_MESSAGE + ": " + e.getMessage());
      return StatusDetail.statusDetail(jobDefinition.jobName(), Status.ERROR, LOAD_JOBS_EXCEPTION_MESSAGE);
    }
  }

  private StatusDetail statusDetailWhenNoJobAvailable(final JobDefinition jobDefinition) {
    return StatusDetail.statusDetail(jobDefinition.jobName(), Status.OK, SUCCESS_MESSAGE);
  }

  protected StatusDetail toStatusDetail(final List<JobInfo> jobInfos, final JobDefinition jobDefinition) {
    final Status status;
    final String message;
    final var lastJob = jobInfos.get(0);
    final var numFailedJobs = getNumFailedJobs(jobInfos);
    switch (lastJob.getStatus()) {
      case OK:
      case SKIPPED:
        if (jobTooOld(lastJob, jobDefinition)) {
          status = Status.WARNING;
          message = jobAgeMessage(jobDefinition);
        } else if (numFailedJobs > maxFailedJobs) {
          status = Status.WARNING;
          message = format(TOO_MANY_JOBS_FAILED_MESSAGE, numFailedJobs, jobInfos.size());
        } else {
          status = Status.OK;
          message = SUCCESS_MESSAGE;
        }
        break;
      case ERROR:
        if (numFailedJobs > maxFailedJobs) {
          status = Status.ERROR;
        } else {
          status = Status.WARNING;
        }
        if (numberOfJobs == 1 && maxFailedJobs <= 1) {
          message = ERROR_MESSAGE;
        } else {
          message = format(TOO_MANY_JOBS_FAILED_MESSAGE, numFailedJobs, jobInfos.size());
        }
        break;

      case DEAD:
      default:
        status = Status.WARNING;
        message = DEAD_MESSAGE;
    }
    return StatusDetail.statusDetail(jobDefinition.jobName(), status, message,
      singletonList(Link.link(REL_JOB, format("%s/jobs/%s", breuningerManagementBasePath, lastJob.getJobId()), "Details")),
      runningDetailsFor(lastJob));
  }

  protected final long getNumFailedJobs(final List<JobInfo> jobInfos) {
    return jobInfos.stream().filter(job -> JobStatus.ERROR == job.getStatus()).count();
  }

  protected Map<String, String> runningDetailsFor(final JobInfo jobInfo) {
    final Map<String, String> details = new HashMap<>();
    details.put("Started", ISO_DATE_TIME.format(jobInfo.getStarted()));
    if (jobInfo.getStopped().isPresent()) {
      details.put("Stopped", ISO_DATE_TIME.format(jobInfo.getStopped().get()));
    }
    return details;
  }

  protected boolean jobTooOld(final JobInfo jobInfo, final JobDefinition jobDefinition) {
    final var stopped = jobInfo.getStopped();
    if (stopped.isPresent() && jobDefinition.maxAge().isPresent()) {
      final var deadlineToRerun = stopped.get().plus(jobDefinition.maxAge().get());
      return deadlineToRerun.isBefore(now());
    }

    return false;
  }

  private String jobAgeMessage(final JobDefinition jobDefinition) {
    return format(JOB_TOO_OLD_MESSAGE,
      jobDefinition.maxAge().isPresent() ? jobDefinition.maxAge().get().getSeconds() + " seconds" : "N/A");
  }

  private void checkArgument(final boolean expression, final String message) {
    if (!expression) {
      throw new IllegalArgumentException(message);
    }
  }
}
