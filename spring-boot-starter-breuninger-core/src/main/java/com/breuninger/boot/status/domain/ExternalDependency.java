package com.breuninger.boot.status.domain;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElseGet;

import java.util.Objects;

import com.breuninger.boot.annotations.Beta;

import net.jcip.annotations.Immutable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Beta
@Immutable
@Getter
@EqualsAndHashCode
@ToString
public class ExternalDependency {

  private final String name;
  private final String description;
  private final String type;
  private final String subtype;
  private final Criticality criticality;
  private final Expectations expectations;

  ExternalDependency(final String name, final String description, final String type, final String subtype,
                     final Criticality criticality, final Expectations expectations) {
    this.name = Objects.toString(name, "");
    this.description = Objects.toString(description, "");
    this.type = requireNonNull(type, "Parameter 'type' must not be null");
    this.subtype = requireNonNull(subtype, "Parameter 'subtype' must not be null");
    this.criticality = requireNonNullElseGet(criticality, Criticality::unspecifiedCriticality);
    this.expectations = requireNonNullElseGet(expectations, Expectations::unspecifiedExpectations);
  }
}
