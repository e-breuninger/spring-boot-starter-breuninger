package com.breuninger.boot.example.jobs;

import static org.slf4j.LoggerFactory.getLogger;

import static com.breuninger.boot.jobs.definition.DefaultJobDefinition.manuallyTriggerableJobDefinition;
import static com.breuninger.boot.jobs.domain.JobMarker.JOB;

import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.breuninger.boot.jobs.definition.JobDefinition;
import com.breuninger.boot.jobs.domain.MetaJobRunnable;
import com.breuninger.boot.jobs.repository.JobMetaRepository;

@Component
public class ExampleMetaJob extends MetaJobRunnable {

  private static final Logger LOG = getLogger(ExampleMetaJob.class);

  private static final String JOB_TYPE = "ExampleMetaJob";

  @Autowired
  public ExampleMetaJob(final JobMetaRepository metaRepository) {
    super(JOB_TYPE, metaRepository);
  }

  @Override
  public JobDefinition getJobDefinition() {
    return manuallyTriggerableJobDefinition(JOB_TYPE,
      "Some stateful Job",
      "A Job that stores some meta data",
      0,
      Optional.empty());
  }

  @Override
  public boolean execute() {
    final var lastEntry = getMetaAsInt("lastEntry", 0);

    for (var i = lastEntry + 1; i <= lastEntry + 10; i++) {
      LOG.info(JOB, "Processing Item {}", i);
      setMeta("lastEntry", i);
    }
    return true;
  }
}
