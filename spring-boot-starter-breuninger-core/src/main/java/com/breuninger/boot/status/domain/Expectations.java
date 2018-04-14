package com.breuninger.boot.status.domain;

import static com.breuninger.boot.status.domain.Availability.HIGH;
import static com.breuninger.boot.status.domain.Availability.LOW;
import static com.breuninger.boot.status.domain.Availability.MEDIUM;
import static com.breuninger.boot.status.domain.Availability.NOT_SPECIFIED;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.breuninger.boot.annotations.Beta;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import net.jcip.annotations.Immutable;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@Beta
@Immutable
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
public final class Expectations {
  public final Availability availability;
  public final Performance performance;

  private Expectations() {
    this(null, null);
  }

  private Expectations(final Availability availability, final Performance performance) {
    this.availability = availability;
    this.performance = performance;
  }

  public static Expectations unspecifiedExpectations() {
    return new Expectations(NOT_SPECIFIED, Performance.NOT_SPECIFIED);
  }

  public static Expectations lowExpectations() {
    return new Expectations(LOW, Performance.LOW);
  }

  public static Expectations mediumExpectations() {
    return new Expectations(MEDIUM, Performance.MEDIUM);
  }

  public static Expectations highExpectations() {
    return new Expectations(HIGH, Performance.HIGH);
  }

  public static Expectations expects(final Availability availability, final Performance performance) {
    return new Expectations(availability, performance);
  }
}
