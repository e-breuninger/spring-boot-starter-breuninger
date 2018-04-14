package com.breuninger.boot.jobs.domain;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public final class JobMessage {

  private final Level level;
  private final String message;
  private final OffsetDateTime timestamp;

  private JobMessage(final Level level, final String message, final OffsetDateTime timestamp) {
    this.level = level;
    this.message = message;
    this.timestamp = timestamp != null ? timestamp.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public static JobMessage jobMessage(final Level level, final String message, final OffsetDateTime ts) {
    return new JobMessage(level, message, ts);
  }
}
