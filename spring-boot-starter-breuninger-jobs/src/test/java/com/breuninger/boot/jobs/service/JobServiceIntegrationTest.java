package com.breuninger.boot.jobs.service;

import static java.time.Instant.now;
import static java.time.ZoneId.systemDefault;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus.DEAD;
import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus.OK;
import static com.breuninger.boot.jobs.domain.JobInfo.newJobInfo;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.breuninger.boot.jobs.domain.JobInfo;
import com.breuninger.boot.jobs.repository.JobRepository;
import com.breuninger.boot.testsupport.TestServer;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestServer.class)
public class JobServiceIntegrationTest {

  private final Clock clock = Clock.systemDefaultZone();
  @Autowired
  JobService jobService;
  @Autowired
  JobRepository jobRepository;

  @Test
  public void shouldFindJobService() {
    assertThat(jobService, is(notNullValue()));
  }

  @Test
  public void shouldKillJobsWithoutUpdateSince() {
    final var toBeKilled = defaultJobInfo("toBeKilled", 75);
    jobRepository.createOrUpdate(toBeKilled);

    jobService.killJobsDeadSince(60);

    final var expectedKilledJob = jobRepository.findOne(toBeKilled.getJobId());
    assertThat(expectedKilledJob.get().isStopped(), is(true));
    assertThat(expectedKilledJob.get().getStatus(), is(DEAD));
  }

  @Test
  public void shouldNotKillJobsThatHaveRecentlyBeenUpdated() {
    final var notToBeKilled = defaultJobInfo("notToBeKilled", 45);
    jobRepository.createOrUpdate(notToBeKilled);

    jobService.killJobsDeadSince(60);

    final var expectedRunningJob = jobRepository.findOne(notToBeKilled.getJobId());
    assertThat(expectedRunningJob.get().isStopped(), is(false));
    assertThat(expectedRunningJob.get().getStatus(), is(OK));
  }

  private JobInfo defaultJobInfo(final String jobId, final int secondsAgo) {
    final var lastUpdated = OffsetDateTime.ofInstant(now(clock).minus(secondsAgo, SECONDS), systemDefault());
    return newJobInfo(jobId, "someJobType", OffsetDateTime.MIN, lastUpdated, Optional.empty(), OK, emptyList(), clock,
      "someHostname");
  }
}
