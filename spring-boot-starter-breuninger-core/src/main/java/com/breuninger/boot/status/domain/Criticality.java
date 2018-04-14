package com.breuninger.boot.status.domain;

import static com.breuninger.boot.status.domain.Level.HIGH;
import static com.breuninger.boot.status.domain.Level.LOW;
import static com.breuninger.boot.status.domain.Level.MEDIUM;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.breuninger.boot.annotations.Beta;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@Beta
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
public final class Criticality {

  public static final Criticality MISSION_CRITICAL = criticality(HIGH, "Mission Critical");
  public static final Criticality BUSINESS_CRITICAL = criticality(HIGH, "Business Critical");
  public static final Criticality FUNCTIONAL_CRITICAL = criticality(MEDIUM, "Functional Critical");
  public static final Criticality NON_CRITICAL = criticality(LOW, "Non Critical");
  public static final Criticality NOT_SPECIFIED = criticality(Level.NOT_SPECIFIED, "Not Specified");

  public final Level level;
  public final String disasterImpact;

  private Criticality() {
    this(null, null);
  }

  private Criticality(final Level level, final String disasterImpact) {
    this.level = level;
    this.disasterImpact = disasterImpact;
  }

  public static Criticality unspecifiedCriticality() {
    return NOT_SPECIFIED;
  }

  public static Criticality nonCritical() {
    return NON_CRITICAL;
  }

  public static Criticality lowCriticality(final String disasterImpact) {
    return criticality(LOW, disasterImpact);
  }

  public static Criticality mediumCriticality(final String disasterImpact) {
    return criticality(MEDIUM, disasterImpact);
  }

  public static Criticality highCriticality(final String disasterImpact) {
    return criticality(HIGH, disasterImpact);
  }

  public static Criticality functionalCritical() {
    return FUNCTIONAL_CRITICAL;
  }

  public static Criticality businessCritical() {
    return BUSINESS_CRITICAL;
  }

  public static Criticality missionCritical() {
    return MISSION_CRITICAL;
  }

  public static Criticality criticality(final Level level, final String disasterImpact) {
    return new Criticality(level, disasterImpact);
  }
}
