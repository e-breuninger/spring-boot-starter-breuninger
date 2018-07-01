package com.breuninger.boot.jobs.repository;

import static java.time.Clock.fixed;
import static java.time.Clock.systemDefaultZone;
import static java.time.OffsetDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;

import static org.assertj.core.util.Lists.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus.ERROR;
import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus.OK;
import static com.breuninger.boot.jobs.domain.JobInfo.builder;
import static com.breuninger.boot.jobs.domain.JobInfo.newJobInfo;
import static com.breuninger.boot.jobs.domain.JobMessage.jobMessage;
import static com.breuninger.boot.testsupport.matcher.OptionalMatchers.isAbsent;
import static com.breuninger.boot.testsupport.matcher.OptionalMatchers.isPresent;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Before;
import org.junit.Test;

import com.breuninger.boot.jobs.domain.JobInfo;
import com.breuninger.boot.jobs.domain.Level;
import com.breuninger.boot.jobs.repository.inmem.InMemJobRepository;

public class InMemJobRepositoryTest {

  private InMemJobRepository repository;
  private final Clock clock = systemDefaultZone();

  @Before
  public void setUp() {
    repository = new InMemJobRepository();
  }

  @Test
  public void shouldFindJobInfoByUri() {
    // given
    final var repository = new InMemJobRepository();

    // when
    final var job = newJobInfo(randomUUID().toString(), "MYJOB", clock, "localhost");
    repository.createOrUpdate(job);

    // then
    assertThat(repository.findOne(job.getJobId()), isPresent());
  }

  @Test
  public void shouldReturnAbsentStatus() {
    final var repository = new InMemJobRepository();
    assertThat(repository.findOne("some-nonexisting-job-id"), isAbsent());
  }

  @Test
  public void shouldNotRemoveRunningJobs() {
    // given
    final var testUri = "test";
    repository.createOrUpdate(newJobInfo(testUri, "FOO", systemDefaultZone(), "localhost"));
    // when
    repository.removeIfStopped(testUri);
    // then
    assertThat(repository.size(), is(1L));
  }

  @Test
  public void shouldNotFailToRemoveMissingJob() {
    // when
    repository.removeIfStopped("foo");
    // then
  }

  @Test
  public void shouldRemoveJob() {
    final var stoppedJob = builder().setJobId("some/job/stopped")
      .setJobType("test")
      .setStarted(now(fixed(Instant.now().minusSeconds(10), systemDefault())))
      .setStopped(now(fixed(Instant.now().minusSeconds(7), systemDefault())))
      .setHostname("localhost")
      .setStatus(OK)
      .build();
    repository.createOrUpdate(stoppedJob);
    repository.createOrUpdate(stoppedJob);

    repository.removeIfStopped(stoppedJob.getJobId());

    assertThat(repository.size(), is(0L));
  }

  @Test
  public void shouldFindAll() {
    // given
    repository.createOrUpdate(newJobInfo("oldest", "FOO", fixed(Instant.now().minusSeconds(1), systemDefault()), "localhost"));
    repository.createOrUpdate(newJobInfo("youngest", "FOO", fixed(Instant.now(), systemDefault()), "localhost"));
    // when
    final var jobInfos = repository.findAll();
    // then
    assertThat(jobInfos.size(), is(2));
    assertThat(jobInfos.get(0).getJobId(), is("youngest"));
    assertThat(jobInfos.get(1).getJobId(), is("oldest"));
  }

  @Test
  public void shouldFindLatestDistinct() {
    // given
    final var now = Instant.now();
    final var eins = newJobInfo("eins", "eins", fixed(now.plusSeconds(10), systemDefault()), "localhost");
    final var zwei = newJobInfo("zwei", "eins", fixed(now.plusSeconds(20), systemDefault()), "localhost");
    final var drei = newJobInfo("drei", "zwei", fixed(now.plusSeconds(30), systemDefault()), "localhost");
    final var vier = newJobInfo("vier", "drei", fixed(now.plusSeconds(40), systemDefault()), "localhost");
    final var fuenf = newJobInfo("fuenf", "drei", fixed(now.plusSeconds(50), systemDefault()), "localhost");

    repository.createOrUpdate(eins);
    repository.createOrUpdate(zwei);
    repository.createOrUpdate(drei);
    repository.createOrUpdate(vier);
    repository.createOrUpdate(fuenf);

    // when
    final var latestDistinct = repository.findLatestJobsDistinct();

    // then
    assertThat(latestDistinct, hasSize(3));
    assertThat(latestDistinct, Matchers.containsInAnyOrder(fuenf, zwei, drei));
  }

  @Test
  public void shouldFindRunningJobsWithoutUpdatedSinceSpecificDate() {
    // given
    repository.createOrUpdate(newJobInfo("deadJob", "FOO", fixed(Instant.now().minusSeconds(10), systemDefault()), "localhost"));
    repository.createOrUpdate(newJobInfo("running", "FOO", fixed(Instant.now(), systemDefault()), "localhost"));

    // when
    final var jobInfos = repository.findRunningWithoutUpdateSince(now().minus(5, ChronoUnit.SECONDS));

    // then
    assertThat(jobInfos, IsCollectionWithSize.hasSize(1));
    assertThat(jobInfos.get(0).getJobId(), is("deadJob"));
  }

  @Test
  public void shouldFindLatestByType() {
    // given
    final var type = "TEST";
    final var otherType = "OTHERTEST";

    repository.createOrUpdate(newJobInfo("oldest", type, fixed(Instant.now().minusSeconds(10), systemDefault()), "localhost"));
    repository.createOrUpdate(newJobInfo("other", otherType, fixed(Instant.now().minusSeconds(5), systemDefault()), "localhost"));
    repository.createOrUpdate(newJobInfo("youngest", type, fixed(Instant.now(), systemDefault()), "localhost"));

    // when
    final var jobInfos = repository.findLatestBy(type, 2);

    // then
    assertThat(jobInfos.get(0).getJobId(), is("youngest"));
    assertThat(jobInfos.get(1).getJobId(), is("oldest"));
    assertThat(jobInfos, hasSize(2));
  }

  @Test
  public void shouldFindLatest() {
    // given
    final var type = "TEST";
    final var otherType = "OTHERTEST";
    repository.createOrUpdate(newJobInfo("oldest", type, fixed(Instant.now().minusSeconds(10), systemDefault()), "localhost"));
    repository.createOrUpdate(newJobInfo("other", otherType, fixed(Instant.now().minusSeconds(5), systemDefault()), "localhost"));
    repository.createOrUpdate(newJobInfo("youngest", type, fixed(Instant.now(), systemDefault()), "localhost"));

    // when
    final var jobInfos = repository.findLatest(2);

    // then
    assertThat(jobInfos.get(0).getJobId(), is("youngest"));
    assertThat(jobInfos.get(1).getJobId(), is("other"));
    assertThat(jobInfos, hasSize(2));
  }

  @Test
  public void shouldFindAllJobsOfSpecificType() {
    // given
    final var type = "TEST";
    final var otherType = "OTHERTEST";
    repository.createOrUpdate(builder().setJobId("1")
      .setJobType(type)
      .setStarted(now(fixed(Instant.now().minusSeconds(10), systemDefault())))
      .setStopped(now(fixed(Instant.now().minusSeconds(7), systemDefault())))
      .setHostname("localhost")
      .setStatus(OK)
      .build());
    repository.createOrUpdate(newJobInfo("2", otherType, systemDefaultZone(), "localhost"));
    repository.createOrUpdate(newJobInfo("3", type, systemDefaultZone(), "localhost"));

    // when
    final var jobsType1 = repository.findByType(type);
    final var jobsType2 = repository.findByType(otherType);

    // then
    assertThat(jobsType1.size(), is(2));
    assertThat(jobsType1.stream().anyMatch(job -> "1".equals(job.getJobId())), is(true));
    assertThat(jobsType1.stream().anyMatch(job -> "3".equals(job.getJobId())), is(true));
    assertThat(jobsType2.size(), is(1));
    assertThat(jobsType2.stream().anyMatch(job -> "2".equals(job.getJobId())), is(true));
  }

  @Test
  public void shouldFindStatusOfJob() {
    // given
    final var type = "TEST";
    final var jobInfo = newJobInfo("1", type, systemDefaultZone(), "localhost");
    repository.createOrUpdate(jobInfo);

    // when
    final var status = repository.findStatus("1");

    // then
    assertThat(status, is(OK));
  }

  @Test
  public void shouldAppendMessageToJobInfo() {

    final var someUri = "someUri";

    // given
    final var jobInfo = newJobInfo(someUri, "TEST", systemDefaultZone(), "localhost");
    repository.createOrUpdate(jobInfo);

    // when
    final var igelMessage = jobMessage(Level.WARNING, "Der Igel ist froh.", now());
    repository.appendMessage(someUri, igelMessage);

    // then
    final var jobInfoFromRepo = repository.findOne(someUri).get();

    assertThat(jobInfoFromRepo.getMessages().size(), is(1));
    assertThat(jobInfoFromRepo.getMessages().get(0), is(igelMessage));
  }

  @Test
  public void shouldUpdateJobStatus() {
    // given
    final var foo = jobInfo("http://localhost/foo", "T_FOO"); //default jobStatus is 'OK'
    repository.createOrUpdate(foo);

    // when
    repository.setJobStatus(foo.getJobId(), ERROR);
    final var status = repository.findStatus("http://localhost/foo");

    // then
    assertThat(status, is(ERROR));
  }

  @Test
  public void shouldUpdateJobLastUpdateTime() {
    // given
    final var foo = jobInfo("http://localhost/foo", "T_FOO");
    repository.createOrUpdate(foo);

    final var myTestTime = OffsetDateTime.of(1979, 2, 5, 1, 2, 3, 1_000_000, ZoneOffset.UTC);

    // when
    repository.setLastUpdate(foo.getJobId(), myTestTime);

    final var jobInfo = repository.findOne(foo.getJobId());

    // then
    assertThat(jobInfo.get().getLastUpdated(), is(myTestTime));
  }

  @Test
  public void shouldClearJobInfos() {
    // given
    final var stoppedJob = builder().setJobId("some/job/stopped")
      .setJobType("test")
      .setStarted(now(fixed(Instant.now().minusSeconds(10), systemDefault())))
      .setStopped(now(fixed(Instant.now().minusSeconds(7), systemDefault())))
      .setHostname("localhost")
      .setStatus(OK)
      .build();
    repository.createOrUpdate(stoppedJob);

    // when
    repository.deleteAll();

    // then
    assertThat(repository.findAll(), is(emptyList()));
  }

  private JobInfo jobInfo(final String jobId, final String type) {
    return newJobInfo(jobId, type, now(), now(), Optional.of(now()), OK,
      asList(jobMessage(Level.INFO, "foo", now()), jobMessage(Level.WARNING, "bar", now())), systemDefaultZone(), "localhost");
  }
}
