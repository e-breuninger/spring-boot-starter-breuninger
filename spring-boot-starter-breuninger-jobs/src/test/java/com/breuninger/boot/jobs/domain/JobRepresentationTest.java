package com.breuninger.boot.jobs.domain;

import static java.time.Clock.fixed;
import static java.time.OffsetDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.util.Collections.emptyList;
import static java.util.Optional.of;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static com.breuninger.boot.jobs.controller.JobRepresentation.representationOf;
import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus.OK;
import static com.breuninger.boot.jobs.domain.JobInfo.newJobInfo;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

public class JobRepresentationTest {

  @Test
  public void shouldCalculateRuntime() {
    final var job = jobInfoWithRuntime(90, ChronoUnit.SECONDS);

    final var jobRepresentation = representationOf(job, null, true, "", "");

    assertThat(jobRepresentation.getStatus(), is("OK"));
    assertThat(jobRepresentation.getRuntime(), is("00:01:30"));
    assertThat(jobRepresentation.getHostname(), is("localhost"));
  }

  @Test
  public void shouldFormatRuntimeBiggerThan24Hours() {
    final var job = jobInfoWithRuntime(25, ChronoUnit.HOURS);

    final var jobRepresentation = representationOf(job, null, true, "", "");

    assertThat(jobRepresentation.getRuntime(), is("> 24h"));
  }

  @Test
  public void shouldFormatRuntimeLessThan24Hours() {
    final var job = jobInfoWithRuntime(23, ChronoUnit.HOURS);

    final var jobRepresentation = representationOf(job, null, true, "", "");

    assertThat(jobRepresentation.getRuntime(), is("23:00:00"));
  }

  private JobInfo jobInfoWithRuntime(final int finishAmount, final ChronoUnit unit) {
    final var clock = fixed(Instant.now(), systemDefault());
    final var startTime = now(clock);
    final var finishedTime = startTime.plus(finishAmount, unit);
    return newJobInfo("foo", "TEST", startTime, finishedTime, of(finishedTime), OK, emptyList(), clock, "localhost");
  }
}
