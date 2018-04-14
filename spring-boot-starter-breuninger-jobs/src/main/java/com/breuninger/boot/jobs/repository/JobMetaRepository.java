package com.breuninger.boot.jobs.repository;

import java.util.Set;

import com.breuninger.boot.jobs.domain.JobMeta;

public interface JobMetaRepository {

  JobMeta getJobMeta(String jobType);

  boolean createValue(String jobType, String key, String value);

  boolean setRunningJob(String jobType, String jobId);

  String getRunningJob(String jobType);

  void clearRunningJob(String jobType);

  void disable(String jobType, String comment);

  void enable(String jobType);

  String setValue(String jobType, String key, String value);

  String getValue(String jobType, String key);

  Set<String> findAllJobTypes();

  void deleteAll();
}
