package com.breuninger.boot.jobs.repository;

import static java.time.Clock.fixed;
import static java.time.OffsetDateTime.now;
import static java.time.ZoneId.systemDefault;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus.SKIPPED;
import static com.breuninger.boot.jobs.domain.JobInfo.builder;
import static com.breuninger.boot.testsupport.matcher.OptionalMatchers.isAbsent;
import static com.breuninger.boot.testsupport.matcher.OptionalMatchers.isPresent;

import java.time.Clock;
import java.time.Instant;

import org.junit.Test;

import com.breuninger.boot.jobs.repository.cleanup.KeepLastJobs;
import com.breuninger.boot.jobs.repository.inmem.InMemJobRepository;

public class DeleteSkippedJobsTest {

  private final Clock now = fixed(Instant.now(), systemDefault());
  private final Clock earlier = fixed(Instant.now().minusSeconds(1), systemDefault());
  private final Clock muchEarlier = fixed(Instant.now().minusSeconds(10), systemDefault());

  @Test
  public void shouldRemoveOldestJobs() {
    // given
    final var job = builder().setJobId("foo")
      .setJobType("TYPE")
      .setStarted(now(now))
      .setStopped(now(now))
      .setHostname("localhost")
      .setStatus(SKIPPED)
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
    final var job = builder().setJobId("foo").setJobType("TYPE").setHostname("localhost").setStatus(SKIPPED).build();

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
}
