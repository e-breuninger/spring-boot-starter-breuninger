package com.breuninger.boot.jobs.eventbus;

import static java.time.Instant.ofEpochMilli;
import static java.time.OffsetDateTime.ofInstant;
import static java.time.ZoneId.systemDefault;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import static com.breuninger.boot.jobs.domain.JobMessage.jobMessage;
import static com.breuninger.boot.jobs.domain.Level.ERROR;
import static com.breuninger.boot.jobs.eventbus.events.StateChangeEvent.State.DEAD;
import static com.breuninger.boot.jobs.eventbus.events.StateChangeEvent.State.FAILED;
import static com.breuninger.boot.jobs.eventbus.events.StateChangeEvent.State.KEEP_ALIVE;
import static com.breuninger.boot.jobs.eventbus.events.StateChangeEvent.State.RESTART;
import static com.breuninger.boot.jobs.eventbus.events.StateChangeEvent.State.SKIPPED;
import static com.breuninger.boot.jobs.eventbus.events.StateChangeEvent.State.STOP;
import static com.breuninger.boot.jobs.eventbus.events.StateChangeEvent.newStateChangeEvent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.breuninger.boot.jobs.definition.JobDefinition;
import com.breuninger.boot.jobs.eventbus.events.StateChangeEvent;
import com.breuninger.boot.jobs.eventbus.events.StateChangeEvent.State;
import com.breuninger.boot.jobs.service.JobRunnable;
import com.breuninger.boot.jobs.service.JobService;

public class PersistenceJobStateChangeListenerTest {

  private static final String JOB_ID = "some/job/id";
  private static final String JOB_TYPE = "jobType";

  @Mock
  private JobService jobServiceMock;
  @Mock
  private JobRunnable jobRunnableMock;

  private PersistenceJobStateChangeListener subject;

  @Before
  public void setUp() {
    initMocks(this);

    when(jobRunnableMock.getJobDefinition()).thenReturn(mock(JobDefinition.class));

    subject = new PersistenceJobStateChangeListener(jobServiceMock);
  }

  @Test
  public void shouldPersistStillAliveEvent() {
    subject.consumeStateChange(stateChangedEvent(KEEP_ALIVE));

    verify(jobServiceMock).keepAlive(JOB_ID);
  }

  @Test
  public void shouldPersistRestartEvent() {
    subject.consumeStateChange(stateChangedEvent(RESTART));

    verify(jobServiceMock).markRestarted(JOB_ID);
  }

  @Test
  public void shouldPersistDeadEvent() {
    final var mockDefinition = mock(JobDefinition.class);
    when(mockDefinition.jobType()).thenReturn(JOB_TYPE);
    when(jobRunnableMock.getJobDefinition()).thenReturn(mockDefinition);
    subject.consumeStateChange(stateChangedEvent(DEAD));

    verify(jobServiceMock).killJob(JOB_ID);
  }

  @Test
  public void shouldPersistStopEvent() {
    subject.consumeStateChange(stateChangedEvent(STOP));

    verify(jobServiceMock).stopJob(JOB_ID);
  }

  @Test
  public void shouldPersistFailedEvent() {
    final var event = stateChangedEvent(FAILED);
    subject.consumeStateChange(event);

    final var ts = ofInstant(ofEpochMilli(event.getTimestamp()), systemDefault());
    verify(jobServiceMock).appendMessage(JOB_ID, jobMessage(ERROR, "", ts));
  }

  @Test
  public void shouldPersistSkippedEvent() {
    subject.consumeStateChange(stateChangedEvent(SKIPPED));

    verify(jobServiceMock).markSkipped(JOB_ID);
  }

  @Test
  public void shouldNotThrowIfStateChangeFailsInDatabase() {
    doThrow(new RuntimeException("Unexpected disturbance in the force")).when(jobServiceMock).stopJob(JOB_ID);
    subject.consumeStateChange(stateChangedEvent(STOP));

    verify(jobServiceMock).stopJob(JOB_ID);
  }

  private StateChangeEvent stateChangedEvent(final State stop) {
    return newStateChangeEvent(jobRunnableMock, JOB_ID, stop);
  }
}
