package com.breuninger.boot.togglz.controller;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@JsonInclude(NON_NULL)
public class FeatureToggleRepresentation {

  public final String description;
  public final boolean enabled;
  public final String value;
}
