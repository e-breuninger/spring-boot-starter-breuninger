package com.breuninger.boot.jobs.service;

import com.breuninger.boot.jobs.definition.JobDefinition;
import com.breuninger.boot.jobs.domain.JobInfo;

public interface JobRunnable {

  JobDefinition getJobDefinition();

  boolean execute();
}
