package com.breuninger.boot.jobs.repository;

import static java.time.Clock.fixed;
import static java.time.OffsetDateTime.now;
import static java.time.ZoneId.systemDefault;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus.ERROR;
import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus.OK;
import static com.breuninger.boot.jobs.domain.JobInfo.builder;
import static com.breuninger.boot.testsupport.matcher.OptionalMatchers.isAbsent;
import static com.breuninger.boot.testsupport.matcher.OptionalMatchers.isPresent;

import java.time.Clock;
import java.time.Instant;

import org.junit.Test;

import com.breuninger.boot.jobs.repository.cleanup.KeepLastJobs;
import com.breuninger.boot.jobs.repository.inmem.InMemJobRepository;

public class KeepLastJobsTest {

  private final Clock now = fixed(Instant.now(), systemDefault());
  private final Clock earlier = fixed(Instant.now().minusSeconds(1), systemDefault());
  private final Clock muchEarlier = fixed(Instant.now().minusSeconds(10), systemDefault());
  private final Clock evenEarlier = fixed(Instant.now().minusSeconds(20), systemDefault());

  @Test
  public void shouldRemoveOldestJobs() {
    // given
    final var job = builder().setJobId("foo")
      .setJobType("TYPE")
      .setStarted(now(now))
      .setStopped(now(now))
      .setHostname("localhost")
      .setStatus(OK)
      .build();

    final JobRepository repository = new InMemJobRepository() {{
      createOrUpdate(job);
      createOrUpdate(job.copy().setJobId("foobar").setStarted(now(earlier)).build());
      createOrUpdate(job.copy().setJobId("bar").setStarted(now(muchEarlier)).build());
    }};
    final var strategy = new KeepLastJobs(repository, 2);

    // when
    strategy.doCleanUp();
    // then
    assertThat(repository.size(), is(2L));
    assertThat(repository.findOne("bar"), isAbsent());
  }

  @Test
  public void shouldOnlyRemoveStoppedJobs() {
    // given
    final var job = builder().setJobId("foo").setJobType("TYPE").setHostname("localhost").setStatus(OK).build();

    final JobRepository repository = new InMemJobRepository() {{
      createOrUpdate(job.copy().setStarted(now(now)).setStopped(now(now)).build());
      createOrUpdate(job.copy().setStarted(now(earlier)).setJobId("foobar").setStarted(now(earlier)).build());
      createOrUpdate(job.copy().setStarted(now(muchEarlier)).setJobId("bar").setStopped(now(now)).build());
    }};
    final var strategy = new KeepLastJobs(repository, 1);

    // when
    strategy.doCleanUp();
    // then
    assertThat(repository.findOne("foo"), isPresent());
    assertThat(repository.findOne("foobar"), isPresent());
    assertThat(repository.findOne("bar"), isAbsent());

    assertThat(repository.size(), is(2L));
  }

  @Test
  public void shouldKeepAtLeastOneSuccessfulJob() {
    // given
    final var job = builder().setJobId("foobar")
      .setJobType("TYPE")
      .setStarted(now(muchEarlier))
      .setStopped(now(now))
      .setHostname("localhost")
      .setStatus(ERROR)
      .build();

    final JobRepository repository = new InMemJobRepository() {{
      createOrUpdate(job.copy().setStatus(OK).build());
      createOrUpdate(job.copy().setStarted(now(now)).setJobId("foo").build());
      createOrUpdate(job.copy().setJobId("bar").setStarted(now(earlier)).build());
      createOrUpdate(job.copy().setJobId("barzig").setStarted(now(evenEarlier)).build());
      createOrUpdate(job.copy().setJobId("foozification").setStarted(now(evenEarlier)).build());
    }};
    final var strategy = new KeepLastJobs(repository, 2);

    // when
    strategy.doCleanUp();
    // then
    assertThat(repository.size(), is(3L));
    assertThat(repository.findOne("foo"), isPresent());
    assertThat(repository.findOne("bar"), isPresent());
    assertThat(repository.findOne("barzig"), isAbsent());
    assertThat(repository.findOne("foobar"), isPresent());
    assertThat(repository.findOne("foozification"), isAbsent());
  }

  @Test
  public void shouldKeepNJobsOfEachTypePresentAndNotRemoveRunningJobs() {
    // given
    final var stoppedJob = builder().setJobId("foo1")
      .setJobType("TYPE1")
      .setStarted(now(now))
      .setStopped(now(now))
      .setHostname("localhost")
      .setStatus(OK)
      .build();

    final JobRepository repository = new InMemJobRepository() {{
      createOrUpdate(stoppedJob);
      createOrUpdate(stoppedJob.copy().setJobId("foo2").setStopped(now(muchEarlier)).setStarted(now(muchEarlier)).build());
      createOrUpdate(stoppedJob.copy().setJobId("foo3").setStopped(now(evenEarlier)).setStarted(now(evenEarlier)).build());
      createOrUpdate(
        stoppedJob.copy().setJobId("bar1").setJobType("TYPE2").setStopped(now(earlier)).setStarted(now(earlier)).build());
      createOrUpdate(
        stoppedJob.copy().setJobId("bar2").setJobType("TYPE2").setStopped(now(muchEarlier)).setStarted(now(muchEarlier)).build());
      createOrUpdate(
        stoppedJob.copy().setJobId("bar3").setJobType("TYPE2").setStopped(now(evenEarlier)).setStarted(now(evenEarlier)).build());
    }};
    final var strategy = new KeepLastJobs(repository, 2);

    // when
    strategy.doCleanUp();
    // then
    assertThat(repository.size(), is(4L));
    assertThat(repository.findByType("TYPE1"), hasSize(2));
    assertThat(repository.findByType("TYPE2"), hasSize(2));
  }

  @Test
  public void shouldBeOkToKeepAllJobs() {
    // given
    final JobRepository repository = new InMemJobRepository() {{
      createOrUpdate(builder().setJobId("foo1")
        .setJobType("TYPE1")
        .setStarted(now(now))
        .setStopped(now(now))
        .setHostname("localhost")
        .setStatus(OK)
        .build());
    }};
    final var strategy = new KeepLastJobs(repository, 2);

    // when
    strategy.doCleanUp();
    // then
    assertThat(repository.size(), is(1L));
  }
}
