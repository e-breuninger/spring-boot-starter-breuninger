package com.breuninger.boot.jobs.service;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.breuninger.boot.jobs.definition.JobDefinition;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JobDefinitionService {

  private final List<JobRunnable> jobRunnables;
  private List<JobDefinition> jobDefinitions = new ArrayList<>();

  public JobDefinitionService(@Autowired(required = false) final List<JobRunnable> jobRunnables) {
    if (jobRunnables == null) {
      this.jobRunnables = emptyList();
    } else {
      this.jobRunnables = jobRunnables;
    }
  }

  @PostConstruct
  void postConstruct() {
    LOG.info("Initializing JobDefinitionService...");
    if (jobRunnables.isEmpty()) {
      jobDefinitions = emptyList();
      LOG.info("No JobDefinitions found in microservice.");
    } else {
      jobDefinitions = jobRunnables.stream().map(JobRunnable::getJobDefinition).collect(toList());
      LOG.info("Found " + jobDefinitions.size() + " JobDefinitions: " +
        jobDefinitions.stream().map(JobDefinition::jobType).collect(toList()));
    }
  }

  public List<JobDefinition> getJobDefinitions() {
    return new ArrayList<>(jobDefinitions);
  }

  public Optional<JobDefinition> getJobDefinition(final String jobType) {
    return jobDefinitions.stream().filter(j -> j.jobType().equalsIgnoreCase(jobType)).findAny();
  }
}
