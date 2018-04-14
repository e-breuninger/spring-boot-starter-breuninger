package com.breuninger.boot.jobs.service;

import static java.time.Duration.ofSeconds;
import static java.util.Optional.empty;
import static java.util.concurrent.TimeUnit.SECONDS;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import static com.breuninger.boot.jobs.definition.DefaultJobDefinition.fixedDelayJobDefinition;
import static com.breuninger.boot.jobs.definition.DefaultJobDefinition.manuallyTriggerableJobDefinition;
import static com.breuninger.boot.jobs.eventbus.events.StateChangeEvent.State.KEEP_ALIVE;
import static com.breuninger.boot.jobs.eventbus.events.StateChangeEvent.State.RESTART;
import static com.breuninger.boot.jobs.eventbus.events.StateChangeEvent.State.SKIPPED;
import static com.breuninger.boot.jobs.eventbus.events.StateChangeEvent.State.START;
import static com.breuninger.boot.jobs.eventbus.events.StateChangeEvent.State.STOP;
import static com.breuninger.boot.jobs.eventbus.events.StateChangeEvent.newStateChangeEvent;
import static com.breuninger.boot.jobs.service.JobRunner.newJobRunner;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;

import com.breuninger.boot.jobs.definition.JobDefinition;

public class JobRunnerTest {
  @Mock
  private ApplicationEventPublisher eventPublisher;
  @Mock
  private ScheduledExecutorService executor;
  @Mock
  private ScheduledFuture<?> scheduledJob;

  @Before
  public void setUp() {
    initMocks(this);

    doReturn(scheduledJob).when(executor).scheduleAtFixedRate(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class));
  }

  @Test
  public void shouldExecuteJob() {
    // given
    final var jobRunnable = mock(JobRunnable.class);
    when(jobRunnable.getJobDefinition()).thenReturn(fixedDelayJobDefinition("TYPE", "", "", ofSeconds(2), 0, empty()));
    final var jobRunner = jobRunner(jobRunnable);

    // when
    jobRunner.run();

    // then
    verify(jobRunnable).execute();
  }

  @Test
  public void shouldSetMDC() {
    // given
    final var jobRunnable = mock(JobRunnable.class);
    when(jobRunnable.getJobDefinition()).thenReturn(fixedDelayJobDefinition("TYPE", "", "", ofSeconds(2), 0, empty()));
    final var jobRunner = jobRunner(jobRunnable);

    // when
    jobRunner.start();

    // then
    assertThat(MDC.get("job_id"), is("42"));
    assertThat(MDC.get("job_type"), is("TYPE"));
  }

  @Test
  public void shouldSendLifecycleEvents() {
    // given
    final var jobRunnable = mock(JobRunnable.class);
    when(jobRunnable.getJobDefinition()).thenReturn(fixedDelayJobDefinition("TYPE", "", "", ofSeconds(2), 0, empty()));
    final var jobRunner = jobRunner(jobRunnable);

    // when
    jobRunner.run();

    // then
    verify(eventPublisher).publishEvent(newStateChangeEvent(jobRunnable, "42", START));
    verify(eventPublisher).publishEvent(newStateChangeEvent(jobRunnable, "42", STOP));
  }

  @Test
  public void shouldMarkJobSkipped() {
    // given
    final var jobRunnable = mock(JobRunnable.class);
    when(jobRunnable.getJobDefinition()).thenReturn(fixedDelayJobDefinition("TYPE", "", "", ofSeconds(2), 0, empty()));
    when(jobRunnable.execute()).thenReturn(false);
    final var jobRunner = jobRunner(jobRunnable);

    // when
    jobRunner.run();

    // then
    verify(eventPublisher).publishEvent(newStateChangeEvent(jobRunnable, "42", SKIPPED));
  }

  @Test
  public void shouldRestartJobOnException() {
    // given
    final var jobRunnable = mock(JobRunnable.class);

    when(jobRunnable.getJobDefinition()).thenReturn(
      manuallyTriggerableJobDefinition("someJobType", "someJobname", "Me is testjob", 2, empty()));
    doThrow(new RuntimeException("some error")).when(jobRunnable).execute();
    final var jobRunner = jobRunner(jobRunnable);

    // when
    jobRunner.run();

    // then
    verify(eventPublisher).publishEvent(newStateChangeEvent(jobRunnable, "42", START));
    verify(jobRunnable, times(3)).execute();
    verify(eventPublisher, times(2)).publishEvent(newStateChangeEvent(jobRunnable, "42", RESTART));
    verify(eventPublisher).publishEvent(newStateChangeEvent(jobRunnable, "42", STOP));
  }

  @Test
  public void shouldSendKeepAliveEventWithinPingJob() {
    // given
    final var jobRunnable = getMockedRunnable();
    final var jobRunner = jobRunner(jobRunnable);

    // when
    jobRunner.run();

    //then
    final var pingRunnableArgumentCaptor = forClass(Runnable.class);
    verify(executor).scheduleAtFixedRate(pingRunnableArgumentCaptor.capture(), eq(20L), eq(20L), eq(SECONDS));

    pingRunnableArgumentCaptor.getValue().run();

    verify(eventPublisher).publishEvent(newStateChangeEvent(jobRunnable, "42", KEEP_ALIVE));
  }

  @Test
  public void shouldStopPingJobWhenJobIsFinished() {
    // given
    final var jobRunnable = getMockedRunnable();
    final var jobRunner = jobRunner(jobRunnable);

    // when
    jobRunner.run();

    //then
    verify(scheduledJob).cancel(false);
  }

  private JobRunnable getMockedRunnable() {
    final var jobRunnable = mock(JobRunnable.class);
    final var jobDefinition = mock(JobDefinition.class);
    when(jobDefinition.jobType()).thenReturn("TYPE");
    when(jobRunnable.getJobDefinition()).thenReturn(jobDefinition);
    return jobRunnable;
  }

  private JobRunner jobRunner(final JobRunnable jobRunnable) {
    return newJobRunner("42", jobRunnable, eventPublisher, executor);
  }
}
