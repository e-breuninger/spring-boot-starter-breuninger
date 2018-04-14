package com.breuninger.boot.jobs.service;

import static java.lang.String.format;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.breuninger.boot.jobs.domain.JobMeta;
import com.breuninger.boot.jobs.domain.RunningJob;
import com.breuninger.boot.jobs.repository.JobBlockedException;
import com.breuninger.boot.jobs.repository.JobMetaRepository;

@Service
public class JobMetaService {

  private final JobMetaRepository jobMetaRepository;
  private final JobMutexGroups mutexGroups;

  public JobMetaService(final JobMetaRepository jobMetaRepository, final JobMutexGroups mutexGroups) {
    this.jobMetaRepository = jobMetaRepository;
    this.mutexGroups = mutexGroups;
  }

  public void aquireRunLock(final String jobId, final String jobType) throws JobBlockedException {
    final var jobMeta = getJobMeta(jobType);
    if (jobMeta.isDisabled()) {
      throw new JobBlockedException(format("Job '%s' is currently disabled", jobType));
    }
    if (jobMetaRepository.setRunningJob(jobType, jobId)) {
      mutexGroups.mutexJobTypesFor(jobType)
        .stream()
        .filter(mutexJobType -> jobMetaRepository.getRunningJob(mutexJobType) != null)
        .findAny()
        .ifPresent(running -> {
          releaseRunLock(jobType);
          throw new JobBlockedException(format("Job '%s' blocked by currently running job '%s'", jobType, running));
        });
    } else {
      throw new JobBlockedException(format("Job '%s' is already running", jobType));
    }
  }

  public void releaseRunLock(final String jobType) {
    jobMetaRepository.clearRunningJob(jobType);
  }

  public Set<RunningJob> runningJobs() {
    final Set<RunningJob> runningJobs = new HashSet<>();
    jobMetaRepository.findAllJobTypes().forEach(jobType -> {
      final var jobId = jobMetaRepository.getRunningJob(jobType);
      if (jobId != null) {
        runningJobs.add(new RunningJob(jobId, jobType));
      }
    });
    return runningJobs;
  }

  public void disable(final String jobType, final String comment) {
    jobMetaRepository.disable(jobType, comment);
  }

  public void enable(final String jobType) {
    jobMetaRepository.enable(jobType);
  }

  public JobMeta getJobMeta(final String jobType) {
    return jobMetaRepository.getJobMeta(jobType);
  }
}
