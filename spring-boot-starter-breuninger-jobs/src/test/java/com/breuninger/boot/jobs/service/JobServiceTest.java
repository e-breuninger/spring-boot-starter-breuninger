package com.breuninger.boot.jobs.service;

import static java.time.Clock.fixed;
import static java.time.Clock.offset;
import static java.time.Instant.now;
import static java.time.ZoneId.systemDefault;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Collections.singletonList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import static com.breuninger.boot.jobs.definition.DefaultJobDefinition.manuallyTriggerableJobDefinition;
import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus.DEAD;
import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus.ERROR;
import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus.OK;
import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus.SKIPPED;
import static com.breuninger.boot.jobs.domain.JobInfo.newJobInfo;
import static com.breuninger.boot.jobs.domain.JobMessage.jobMessage;
import static com.breuninger.boot.jobs.domain.Level.INFO;
import static com.breuninger.boot.status.domain.SystemInfo.systemInfo;

import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationEventPublisher;

import com.breuninger.boot.jobs.definition.JobDefinition;
import com.breuninger.boot.jobs.domain.JobInfo;
import com.breuninger.boot.jobs.domain.JobInfo.Builder;
import com.breuninger.boot.jobs.domain.Level;
import com.breuninger.boot.jobs.repository.JobBlockedException;
import com.breuninger.boot.jobs.repository.JobRepository;
import com.breuninger.boot.status.domain.SystemInfo;

import io.micrometer.core.instrument.Metrics;

public class JobServiceTest {

  private static final String HOSTNAME = "HOST";
  private static final String JOB_ID = "JOB/ID";
  private static final String JOB_TYPE = "JOB_TYPE";

  @Mock
  private ScheduledExecutorService executorService;
  @Mock
  private ApplicationEventPublisher applicationEventPublisher;
  @Mock
  private JobRunnable jobRunnable;
  @Mock
  private JobRepository jobRepository;
  @Mock
  private UuidProvider uuidProviderMock;
  @Mock
  private JobMetaService jobMetaService;
  private JobService jobService;
  private SystemInfo systemInfo;
  private Clock clock;

  @Before
  public void setUp() {
    initMocks(this);
    systemInfo = systemInfo(HOSTNAME, 8080);

    clock = fixed(now(), systemDefault());

    doAnswer(new RunImmediately()).when(executorService).execute(any(Runnable.class));
    when(executorService.scheduleAtFixedRate(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(
      mock(ScheduledFuture.class));
    when(jobRunnable.getJobDefinition()).thenReturn(
      manuallyTriggerableJobDefinition("someType", "bla", "bla", 0, Optional.empty()));
    when(uuidProviderMock.getUuid()).thenReturn(JOB_ID);
    jobService = new JobService(jobRepository, jobMetaService, singletonList(jobRunnable), executorService,
      applicationEventPublisher, clock, systemInfo, uuidProviderMock);
    jobService.postConstruct();
  }

  @Test
  public void shouldReturnCreatedJobId() {
    // given:
    when(jobRunnable.getJobDefinition()).thenReturn(someJobDefinition("BAR"));

    // when:
    final var jobId = jobService.startAsyncJob("BAR");
    // then:
    assertThat(jobId.isPresent(), is(true));
    assertThat(jobId.get(), not(isEmptyOrNullString()));
  }

  @Test
  public void shouldRunJob() {
    // given:
    final var jobType = "bar";
    when(jobRunnable.getJobDefinition()).thenReturn(someJobDefinition(jobType));

    // when:
    final var optionalJobId = jobService.startAsyncJob(jobType);

    // then:
    final var expectedJobInfo = newJobInfo(optionalJobId.get(), jobType, clock, systemInfo.hostname);
    verify(executorService).execute(any(Runnable.class));
    verify(jobRepository).createOrUpdate(expectedJobInfo);
    verify(jobRunnable).execute();
    verify(jobMetaService).aquireRunLock(expectedJobInfo.getJobId(), expectedJobInfo.getJobType());
  }

  @Test
  public void shouldNotStartJobOnBlockedException() {
    doAnswer(x -> {
      throw new JobBlockedException("");
    }).when(jobMetaService).aquireRunLock(anyString(), anyString());

    final var jobUri = jobService.startAsyncJob("someType");

    assertThat(jobUri.isPresent(), is(false));
    verify(jobRepository, never()).createOrUpdate(any());
  }

  // @Test FIXME
  public void shouldReportRuntime() {
    // given:
    when(jobRunnable.getJobDefinition()).thenReturn(someJobDefinition("BAR"));

    // when:
    jobService.startAsyncJob("BAR");
    // then:
    assertThat(Metrics.summary("gauge.jobs.runtime.bar").totalAmount(), is(greaterThan(0.0d)));
  }

  @Test
  public void shouldStopJob() {
    final var now = OffsetDateTime.now(clock);
    final var earlierClock = offset(clock, Duration.of(-1, MINUTES));
    final var jobInfo = newJobInfo("superId", "superType", earlierClock, HOSTNAME);
    when(jobRepository.findOne("superId")).thenReturn(Optional.of(jobInfo));

    jobService.stopJob("superId");

    final var expected = jobInfo.copy().setStatus(OK).setStopped(now).setLastUpdated(now).build();
    verify(jobMetaService).releaseRunLock("superType");
    verify(jobRepository).createOrUpdate(expected);
  }

  @Test
  public void shouldKillJob() {
    final var now = OffsetDateTime.now(clock);
    final var jobInfo = newJobInfo("superId", "superType", clock, HOSTNAME);
    when(jobRepository.findOne("superId")).thenReturn(Optional.of(jobInfo));

    jobService.killJob("superId");

    final var expected = jobInfo.copy().setStatus(DEAD).setStopped(now).setLastUpdated(now).build();
    verify(jobMetaService).releaseRunLock("superType");
    verify(jobRepository).createOrUpdate(expected);
  }

  @Test
  public void shouldKillDeadJobsSince() {
    final var someJobInfo = defaultJobInfo().setJobType("jobType").build();
    when(jobRepository.findRunningWithoutUpdateSince(any())).thenReturn(singletonList(someJobInfo));
    when(jobRepository.findOne(someJobInfo.getJobId())).thenReturn(Optional.of(someJobInfo));

    jobService.killJobsDeadSince(60);

    verify(jobMetaService).releaseRunLock("jobType");
  }

  @Test
  public void shouldUpdateTimeStampOnKeepAlive() {
    // when
    jobService.keepAlive(JOB_ID);

    // then
    final var now = OffsetDateTime.now(clock);
    verify(jobRepository).setLastUpdate(JOB_ID, now);
  }

  @Test
  public void shouldMarkSkipped() {
    // when
    jobService.markSkipped(JOB_ID);

    // then
    final var now = OffsetDateTime.now(clock);

    verify(jobRepository).appendMessage(JOB_ID, jobMessage(INFO, "Skipped job ..", now));
    verify(jobRepository).setJobStatus(JOB_ID, SKIPPED);
  }

  @Test
  public void shouldMarkRestarted() {
    // when
    jobService.markRestarted(JOB_ID);

    // then
    final var now = OffsetDateTime.now(clock);

    verify(jobRepository).appendMessage(JOB_ID, jobMessage(Level.WARNING, "Restarting job ..", now));
    verify(jobRepository).setJobStatus(JOB_ID, OK);
  }

  @Test
  public void shouldAppendNonErrorMessage() {
    final var message = jobMessage(INFO, "This is an interesting message", OffsetDateTime.now());

    // when
    jobService.appendMessage(JOB_ID, message);

    // then
    verify(jobRepository).appendMessage(JOB_ID, message);
    verify(jobRepository, never()).createOrUpdate(any(JobInfo.class));
  }

  @Test
  public void shouldAppendErrorMessageAndSetErrorStatus() {
    final var now = OffsetDateTime.now(clock);
    final var earlier = now.minus(10, MINUTES);
    final var message = jobMessage(Level.ERROR, "Error: Out of hunk", now);
    final var jobInfo = defaultJobInfo().setLastUpdated(earlier).build();
    when(jobRepository.findOne(JOB_ID)).thenReturn(Optional.of(jobInfo));

    // when
    jobService.appendMessage(JOB_ID, message);

    // then
    verify(jobRepository).appendMessage(JOB_ID, message);
    verify(jobRepository).setJobStatus(JOB_ID, ERROR);
  }

  private Builder defaultJobInfo() {
    return newJobInfo(JOB_ID, JOB_TYPE, clock, HOSTNAME).copy();
  }

  private JobDefinition someJobDefinition(final String jobType) {
    return new JobDefinition() {
      @Override
      public String jobType() {
        return jobType;
      }

      @Override
      public String jobName() {
        return "test";
      }

      @Override
      public String description() {
        return "test";
      }
    };
  }

  private static class RunImmediately implements Answer<Object> {
    @Override
    public Object answer(final InvocationOnMock invocation) {
      final var runnable = (Runnable) invocation.getArguments()[0];
      runnable.run();
      return null;
    }
  }
}
