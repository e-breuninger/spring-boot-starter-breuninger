package com.breuninger.boot.jobs.status;

import static java.time.Duration.ofSeconds;
import static java.time.OffsetDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.breuninger.boot.jobs.definition.DefaultJobDefinition.fixedDelayJobDefinition;
import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus.DEAD;
import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus.ERROR;
import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus.OK;
import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus.SKIPPED;
import static com.breuninger.boot.jobs.status.JobStatusCalculator.errorOnLastJobFailed;
import static com.breuninger.boot.jobs.status.JobStatusCalculator.errorOnLastNumJobsFailed;
import static com.breuninger.boot.jobs.status.JobStatusCalculator.warningOnLastJobFailed;

import org.junit.Before;
import org.junit.Test;

import com.breuninger.boot.jobs.definition.JobDefinition;
import com.breuninger.boot.jobs.domain.JobInfo;
import com.breuninger.boot.jobs.domain.JobInfo.JobStatus;
import com.breuninger.boot.jobs.repository.JobRepository;
import com.breuninger.boot.status.domain.Status;

public class JobStatusCalculatorTest {

  private final JobDefinition jobDefinition = fixedDelayJobDefinition("test", "test", "", ofSeconds(10), 0, of(ofSeconds(10)));
  private JobRepository jobRepository;
  private JobStatusCalculator warningOnLastJobFailed;
  private JobStatusCalculator errorOnLastJobFailed;
  private JobStatusCalculator errorOnLastTwoJobsFailed;

  @Before
  public void setUp() {
    jobRepository = mock(JobRepository.class);
    warningOnLastJobFailed = warningOnLastJobFailed("test", jobRepository, "/someInternalPath");
    errorOnLastJobFailed = errorOnLastJobFailed("test", jobRepository, "/someInternalPath");
    errorOnLastTwoJobsFailed = errorOnLastNumJobsFailed("test", 2, jobRepository, "/someInternalPath");
  }

  @Test
  public void shouldIndicateOkIfLastJobOk() {
    // given
    final var jobs = singletonList(someStoppedJob(OK, 1));
    when(jobRepository.findLatestBy(anyString(), eq(1))).thenReturn(jobs);

    // when
    final var first = errorOnLastJobFailed.statusDetail(jobDefinition);
    final var second = warningOnLastJobFailed.statusDetail(jobDefinition);

    // then
    assertThat(first.getStatus(), is(Status.OK));
    assertThat(first.getMessage(), is("Last job was successful"));
    assertThat(second.getStatus(), is(Status.OK));
  }

  @Test
  public void shouldIndicateOkIfLastJobSkipped() {
    // given
    final var jobs = singletonList(someStoppedJob(SKIPPED, 1));
    when(jobRepository.findLatestBy(anyString(), eq(1))).thenReturn(jobs);

    // when
    final var first = errorOnLastJobFailed.statusDetail(jobDefinition);
    final var second = warningOnLastJobFailed.statusDetail(jobDefinition);

    // then
    assertThat(first.getStatus(), is(Status.OK));
    assertThat(first.getMessage(), is("Last job was successful"));
    assertThat(second.getStatus(), is(Status.OK));
  }

  @Test
  public void shouldReturnStatusDetailWithJobLink() {
    // given
    final var jobInfo = someStoppedJob(OK, 1);
    final var jobs = singletonList(jobInfo);
    when(jobRepository.findLatestBy(anyString(), eq(1))).thenReturn(jobs);

    // when
    final var first = errorOnLastJobFailed.statusDetail(jobDefinition);

    // then
    assertThat(first.getLinks().size(), is(1));
    assertThat(first.getLinks().get(0).href, is("/someInternalPath/jobs/" + jobInfo.getJobId()));
  }

  @Test
  public void shouldIndicateStateIfLastJobFailed() {
    // given
    final var jobInfos = singletonList(someStoppedJob(ERROR, 1));
    when(jobRepository.findLatestBy(anyString(), eq(1))).thenReturn(jobInfos);

    // when
    final var first = errorOnLastJobFailed.statusDetail(jobDefinition);
    final var second = warningOnLastJobFailed.statusDetail(jobDefinition);

    // then
    assertThat(first.getStatus(), is(Status.ERROR));
    assertThat(first.getMessage(), is("Job had an error"));
    assertThat(second.getStatus(), is(Status.WARNING));
    assertThat(second.getMessage(), is("Job had an error"));
  }

  @Test
  public void shouldIndicateOkIfLastOfTwoJobsOk() {
    // given
    final var jobInfos = asList(someStoppedJob(OK, 1), someStoppedJob(ERROR, 2));
    when(jobRepository.findLatestBy(anyString(), eq(2))).thenReturn(jobInfos);

    // when
    final var detail = errorOnLastTwoJobsFailed.statusDetail(jobDefinition);

    // then
    assertThat(detail.getStatus(), is(Status.OK));
    assertThat(detail.getMessage(), is("Last job was successful"));
  }

  @Test
  public void shouldIndicateWarningIfOneOfTwoJobsOk() {
    // given
    final var jobInfos = asList(someStoppedJob(ERROR, 1), someStoppedJob(OK, 2));
    when(jobRepository.findLatestBy(anyString(), eq(2))).thenReturn(jobInfos);

    // when
    final var detail = errorOnLastTwoJobsFailed.statusDetail(jobDefinition);

    // then
    assertThat(detail.getStatus(), is(Status.WARNING));
    assertThat(detail.getMessage(), is("1 out of 2 job executions failed"));
  }

  @Test
  public void shouldIndicateWarningIfTwoOfThreeJobsFailed() {
    // given
    final var jobInfos = asList(someStoppedJob(OK, 1), someStoppedJob(ERROR, 2), someStoppedJob(ERROR, 2));
    when(jobRepository.findLatestBy(anyString(), eq(3))).thenReturn(jobInfos);

    // when
    final var maxOneOfThree = new JobStatusCalculator("test", 3, 1, jobRepository, "/someInternalPath");
    final var detail = maxOneOfThree.statusDetail(jobDefinition);

    // then
    assertThat(detail.getStatus(), is(Status.WARNING));
    assertThat(detail.getMessage(), is("2 out of 3 job executions failed"));
  }

  @Test
  public void shouldIndicateErrorIfTwoOfThreeJobsFailed() {
    // given
    final var jobInfos = asList(someStoppedJob(ERROR, 1), someStoppedJob(OK, 2), someStoppedJob(ERROR, 2));
    when(jobRepository.findLatestBy(anyString(), eq(3))).thenReturn(jobInfos);

    // when
    final var maxOneOfThree = new JobStatusCalculator("test", 3, 1, jobRepository, "/someInternalPath");
    final var detail = maxOneOfThree.statusDetail(jobDefinition);

    // then
    assertThat(detail.getStatus(), is(Status.ERROR));
    assertThat(detail.getMessage(), is("2 out of 3 job executions failed"));
  }

  @Test
  public void shouldIndicateErrorIfTwoJobsFailed() {
    // given
    final var jobInfos = asList(someStoppedJob(ERROR, 1), someStoppedJob(ERROR, 2));
    when(jobRepository.findLatestBy(anyString(), eq(2))).thenReturn(jobInfos);

    // when
    final var detail = errorOnLastTwoJobsFailed.statusDetail(jobDefinition);

    // then
    assertThat(detail.getStatus(), is(Status.ERROR));
    assertThat(detail.getMessage(), is("2 out of 2 job executions failed"));
  }

  @Test
  public void shouldIndicateWarningIfLastJobRunWasTooLongAgo() {
    // given
    final var jobInfo = singletonList(someStoppedJob(OK, 11));
    final var jobInfos = asList(someStoppedJob(OK, 11), someStoppedJob(OK, 12));
    when(jobRepository.findLatestBy(anyString(), eq(1))).thenReturn(jobInfo);
    when(jobRepository.findLatestBy(anyString(), eq(2))).thenReturn(jobInfos);

    // when
    final var first = errorOnLastJobFailed.statusDetail(jobDefinition);
    final var second = warningOnLastJobFailed.statusDetail(jobDefinition);
    final var third = errorOnLastTwoJobsFailed.statusDetail(jobDefinition);

    // then
    assertThat(first.getStatus(), is(Status.WARNING));
    assertThat(first.getMessage(), is("Job didn't run in the past 10 seconds"));
    assertThat(second.getStatus(), is(Status.WARNING));
    assertThat(second.getMessage(), is("Job didn't run in the past 10 seconds"));
    assertThat(third.getStatus(), is(Status.WARNING));
    assertThat(third.getMessage(), is("Job didn't run in the past 10 seconds"));
  }

  @Test
  public void shouldNotHaveUriOrRunningIfNoJobPresent() {
    // given
    when(jobRepository.findLatestBy(anyString(), eq(1))).thenReturn(emptyList());

    // when
    final var statusDetail = errorOnLastJobFailed.statusDetail(jobDefinition);

    // then
    assertThat(statusDetail.getLinks(), is(emptyList()));
    assertThat(statusDetail.getDetails(), not(hasKey("Running")));
  }

  @Test
  public void shouldIndicateThatJobIsNotRunning() {
    // given
    final var jobInfos = singletonList(someStoppedJob(OK, 1));
    when(jobRepository.findLatestBy(anyString(), eq(1))).thenReturn(jobInfos);

    // when
    final var statusDetail = errorOnLastJobFailed.statusDetail(jobDefinition);

    // then
    assertThat(statusDetail.getDetails(), not(hasKey("Running")));
    assertThat(statusDetail.getDetails(), hasKey("Stopped"));
  }

  @Test
  public void shouldIndicateWarningIfJobRunWasDead() {
    // given
    final var jobInfos = singletonList(someRunningJob(DEAD, 1));
    when(jobRepository.findLatestBy(anyString(), eq(1))).thenReturn(jobInfos);

    // when
    final var statusDetail = errorOnLastJobFailed.statusDetail(jobDefinition);

    // then
    assertThat(statusDetail.getStatus(), is(Status.WARNING));
    assertThat(statusDetail.getMessage(), is("Job died"));
  }

  @Test
  public void shouldIndicateWarningIfLastJobRunWasDead() {
    // given
    final var jobInfos = singletonList(someStoppedJob(DEAD, 1));
    when(jobRepository.findLatestBy(anyString(), eq(1))).thenReturn(jobInfos);

    // when
    final var statusDetail = errorOnLastJobFailed.statusDetail(jobDefinition);

    // then
    assertThat(statusDetail.getStatus(), is(Status.WARNING));
    assertThat(statusDetail.getMessage(), is("Job died"));
  }

  @Test
  public void shouldIndicateErrorIfJobCouldNotBeRetievedFromRepository() {
    // given
    when(jobRepository.findLatestBy(anyString(), eq(1))).thenThrow(RuntimeException.class);

    // when
    final var statusDetail = errorOnLastJobFailed.statusDetail(jobDefinition);

    // then
    assertThat(statusDetail.getStatus(), is(Status.ERROR));
  }

  @Test
  public void shouldAcceptIfNoJobRan() {
    // given
    when(jobRepository.findLatestBy(anyString(), eq(1))).thenReturn(emptyList());

    // when
    final var statusDetail = errorOnLastJobFailed.statusDetail(jobDefinition);

    // then
    assertThat(statusDetail.getStatus(), is(Status.OK));
  }

  @Test
  public void shouldHaveName() {
    // given
    final var jobInfos = singletonList(someStoppedJob(OK, 1));
    when(jobRepository.findLatestBy(anyString(), eq(1))).thenReturn(jobInfos);

    // when
    final var statusDetail = errorOnLastJobFailed.statusDetail(jobDefinition);

    // then
    assertThat(statusDetail.getName(), is("test"));
  }

  private JobInfo someStoppedJob(final JobStatus jobStatus, final int startedSecondsAgo) {
    final var now = now();
    final var someJob = mock(JobInfo.class);
    when(someJob.getJobType()).thenReturn("someJobType");
    when(someJob.getJobId()).thenReturn("someId");
    when(someJob.getStarted()).thenReturn(now.minusSeconds(startedSecondsAgo));
    when(someJob.getStopped()).thenReturn(of(now.minusSeconds(startedSecondsAgo - 1)));
    when(someJob.getStatus()).thenReturn(jobStatus);
    return someJob;
  }

  private JobInfo someRunningJob(final JobStatus jobStatus, final int startedSecondsAgo) {
    final var now = now();
    final var someJob = mock(JobInfo.class);
    when(someJob.getJobType()).thenReturn("someJobType");
    when(someJob.getJobId()).thenReturn("someJobId");
    when(someJob.getStarted()).thenReturn(now.minusSeconds(startedSecondsAgo));
    when(someJob.getStopped()).thenReturn(empty());
    when(someJob.getStatus()).thenReturn(jobStatus);
    return someJob;
  }
}
