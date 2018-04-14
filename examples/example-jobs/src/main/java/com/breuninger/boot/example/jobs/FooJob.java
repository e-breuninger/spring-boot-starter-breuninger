package com.breuninger.boot.example.jobs;

import static java.lang.Thread.sleep;
import static java.time.Duration.ofHours;

import static org.slf4j.LoggerFactory.getLogger;

import static com.breuninger.boot.jobs.definition.DefaultJobDefinition.fixedDelayJobDefinition;

import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.breuninger.boot.jobs.definition.JobDefinition;
import com.breuninger.boot.jobs.domain.JobMarker;
import com.breuninger.boot.jobs.service.JobRunnable;

@Component
public class FooJob implements JobRunnable {

  private static final Logger LOG = getLogger(FooJob.class);

  @Override
  public JobDefinition getJobDefinition() {
    return fixedDelayJobDefinition("Foo",
      "Foo Job",
      "An example job that is running for a while.",
      ofHours(1),
      0,
      Optional.of(ofHours(3)));
  }

  @Override
  public boolean execute() {
    for (var i = 0; i < 60; ++i) {
      doSomeHardWork();
    }
    return true;
  }

  private void doSomeHardWork() {
    try {
      LOG.info(JobMarker.JOB, "Still doing some hard work...");
      sleep(new Random(42).nextInt(2000));
    } catch (final InterruptedException e) {
      LOG.error(JobMarker.JOB, e.getMessage());
    }
  }
}
