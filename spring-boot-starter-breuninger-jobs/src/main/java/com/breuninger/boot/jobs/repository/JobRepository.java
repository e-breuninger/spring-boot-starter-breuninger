package com.breuninger.boot.jobs.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import com.breuninger.boot.jobs.domain.JobInfo;
import com.breuninger.boot.jobs.domain.JobInfo.JobStatus;
import com.breuninger.boot.jobs.domain.JobMessage;

public interface JobRepository {

  Optional<JobInfo> findOne(String jobId);

  List<JobInfo> findLatest(int maxCount);

  List<JobInfo> findLatestJobsDistinct();

  List<JobInfo> findLatestBy(String type, int maxCount);

  List<JobInfo> findRunningWithoutUpdateSince(OffsetDateTime timeOffset);

  List<JobInfo> findAll();

  List<JobInfo> findAllJobInfoWithoutMessages();

  List<JobInfo> findByType(String jobType);

  JobInfo createOrUpdate(JobInfo job);

  void removeIfStopped(String jobId);

  JobStatus findStatus(String jobId);

  void appendMessage(String jobId, JobMessage jobMessage);

  void setJobStatus(String jobId, JobStatus jobStatus);

  void setLastUpdate(String jobId, OffsetDateTime lastUpdate);

  long size();

  void deleteAll();
}
