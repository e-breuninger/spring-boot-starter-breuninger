package com.breuninger.boot.testsupport.util;

import static java.time.ZoneId.systemDefault;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;

public final class TestClock extends Clock {

  private Instant current;

  private TestClock(final Instant current) {
    this.current = current;
  }

  public static TestClock now() {
    return new TestClock(Instant.now());
  }

  public static TestClock now(final Clock clock) {
    return new TestClock(Instant.now(clock));
  }

  public static TestClock now(final long millis) {
    return new TestClock(Instant.ofEpochMilli(millis));
  }

  @Override
  public ZoneId getZone() {
    return systemDefault();
  }

  @Override
  public Clock withZone(final ZoneId zone) {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public Instant instant() {
    return current;
  }

  public void proceed(final long amount, final TemporalUnit unit) {
    current = current.plus(amount, unit);
  }
}
