package com.breuninger.boot.jobs.domain;

import static java.time.OffsetDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import static com.breuninger.boot.jobs.domain.JobInfo.JobStatus.OK;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.jcip.annotations.ThreadSafe;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@ThreadSafe
public class JobInfo {

  private final String jobId;
  private final String jobType;
  private final OffsetDateTime started;
  private final List<JobMessage> messages;
  private final Optional<OffsetDateTime> stopped;
  private final JobStatus status;
  private final OffsetDateTime lastUpdated;
  private final String hostname;
  private final Clock clock;

  private JobInfo(final String jobType, final String jobId, final Clock clock, final String hostname) {
    this.jobId = jobId;
    this.jobType = jobType;
    started = now(clock).truncatedTo(ChronoUnit.MILLIS);
    this.clock = clock;
    stopped = empty();
    status = OK;
    lastUpdated = started;
    this.hostname = hostname;
    messages = emptyList();
  }

  private JobInfo(final String jobId, final String jobType, final OffsetDateTime started, final OffsetDateTime lastUpdated,
                  final Optional<OffsetDateTime> stopped, final JobStatus status, final List<JobMessage> messages,
                  final Clock clock, final String hostname) {
    this.jobId = jobId;
    this.jobType = jobType;
    this.started = started != null ? started.truncatedTo(ChronoUnit.MILLIS) : null;
    this.lastUpdated = lastUpdated != null ? lastUpdated.truncatedTo(ChronoUnit.MILLIS) : null;
    this.stopped = stopped.map(offsetDateTime -> offsetDateTime.truncatedTo(ChronoUnit.MILLIS));
    this.status = status;
    this.messages = unmodifiableList(messages);
    this.hostname = hostname;
    this.clock = clock;
  }

  public static JobInfo newJobInfo(final String jobId, final String jobType, final Clock clock, final String hostname) {
    return new JobInfo(jobType, jobId, clock, hostname);
  }

  public static JobInfo newJobInfo(final String jobId, final String jobType, final OffsetDateTime started,
                                   final OffsetDateTime lastUpdated, final Optional<OffsetDateTime> stopped,
                                   final JobStatus status, final List<JobMessage> messages, final Clock clock,
                                   final String hostname) {
    return new JobInfo(jobId, jobType, started, lastUpdated, stopped, status, messages, clock, hostname);
  }

  public static Builder builder() {
    return new Builder();
  }

  public synchronized boolean isStopped() {
    return stopped.isPresent();
  }

  public Builder copy() {
    return new Builder(jobId, jobType, started, new ArrayList<>(messages), stopped, status, lastUpdated, hostname, clock);
  }

  public enum JobStatus {OK, SKIPPED, ERROR, DEAD}

  @NoArgsConstructor
  public static final class Builder {
    private String jobId;
    private String jobType;
    private OffsetDateTime started;
    private List<JobMessage> messages = new ArrayList<>();
    private Clock clock;
    private OffsetDateTime stopped;
    private JobStatus status;
    private OffsetDateTime lastUpdated;
    private String hostname;

    public Builder(final String jobId, final String jobType, final OffsetDateTime started, final List<JobMessage> messages,
                   final Optional<OffsetDateTime> stopped, final JobStatus status, final OffsetDateTime lastUpdated,
                   final String hostname, final Clock clock) {
      this.jobId = jobId;
      this.jobType = jobType;
      this.started = started;
      this.messages = messages;
      this.clock = clock;
      this.stopped = stopped.orElse(null);
      this.status = status;
      this.lastUpdated = lastUpdated;
      this.hostname = hostname;
    }

    public Builder setJobId(final String jobId) {
      this.jobId = jobId;
      return this;
    }

    public Builder setClock(final Clock clock) {
      this.clock = clock;
      return this;
    }

    public Builder setJobType(final String jobType) {
      this.jobType = jobType;
      return this;
    }

    public Builder setStarted(final OffsetDateTime started) {
      this.started = started;
      return this;
    }

    public Builder setMessages(final List<JobMessage> messages) {
      this.messages = messages;
      return this;
    }

    public Builder setStopped(final OffsetDateTime stopped) {
      this.stopped = stopped;
      return this;
    }

    public Builder setStatus(final JobStatus status) {
      this.status = status;
      return this;
    }

    public Builder setLastUpdated(final OffsetDateTime lastUpdated) {
      this.lastUpdated = lastUpdated;
      return this;
    }

    public Builder setHostname(final String hostname) {
      this.hostname = hostname;
      return this;
    }

    public JobInfo build() {
      return new JobInfo(jobId, jobType, started, lastUpdated, ofNullable(stopped), status, messages, clock, hostname);
    }

    public Builder addMessage(final JobMessage jobMessage) {
      messages.add(jobMessage);
      return this;
    }
  }
}
