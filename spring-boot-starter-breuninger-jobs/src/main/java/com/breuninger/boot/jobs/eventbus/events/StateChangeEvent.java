package com.breuninger.boot.jobs.eventbus.events;

import org.springframework.context.ApplicationEvent;

import com.breuninger.boot.jobs.service.JobRunnable;

import net.jcip.annotations.Immutable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Immutable
@Getter
@EqualsAndHashCode
@ToString
public class StateChangeEvent extends ApplicationEvent {

  private final String jobId;
  private final String jobType;
  private final State state;
  private final String message;

  private StateChangeEvent(final JobRunnable jobRunnable, final String jobId, final State state, final String message) {
    super(jobRunnable);
    this.jobId = jobId;
    jobType = jobRunnable.getJobDefinition().jobType();
    this.state = state;
    this.message = message;
  }

  public static StateChangeEvent newStateChangeEvent(final JobRunnable jobRunnable, final String jobId, final State state) {
    return new StateChangeEvent(jobRunnable, jobId, state, "");
  }

  public static StateChangeEvent newStateChangeEvent(final JobRunnable jobRunnable, final String jobId, final State state,
                                                     final String message) {
    return new StateChangeEvent(jobRunnable, jobId, state, message);
  }

  public enum State {
    START, STOP, FAILED, SKIPPED, RESTART, KEEP_ALIVE, DEAD
  }
}
