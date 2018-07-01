package com.breuninger.boot.validation.web;

import static de.otto.edison.hal.Link.profile;
import static de.otto.edison.hal.Links.linkingTo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.breuninger.boot.validation.web.ErrorHalRepresentation.Builder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import de.otto.edison.hal.HalRepresentation;

@EqualsAndHashCode(callSuper = true)
@ToString
@JsonDeserialize(builder = Builder.class)
public class ErrorHalRepresentation extends HalRepresentation {

  private static final String PROFILE_ERROR = "http://spec.breuninger.de/profiles/error";

  private final String errorMessage;
  private final Map<String, List<Map<String, String>>> errors;

  private ErrorHalRepresentation(final Builder builder) {
    super(linkingTo().single(profile(PROFILE_ERROR)).build());
    errors = builder.errors;
    errorMessage = builder.errorMessage;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Map<String, List<Map<String, String>>> getErrors() {
    return errors;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static final class Builder {
    private Map<String, List<Map<String, String>>> errors = new HashMap<>();
    private String errorMessage;

    private Builder() {
    }

    public ErrorHalRepresentation build() {
      return new ErrorHalRepresentation(this);
    }

    public Builder withErrors(final Map<String, List<Map<String, String>>> errors) {
      this.errors = errors;
      return this;
    }

    public Builder withError(final String field, final String key, final String message, final String rejected) {
      final Map<String, String> innerMap = new HashMap<>();
      innerMap.put("key", key);
      innerMap.put("message", message);
      innerMap.put("rejected", rejected);
      final var list = errors.getOrDefault(field, new ArrayList<>());
      list.add(innerMap);
      errors.put(field, list);
      return this;
    }

    public Builder withErrorMessage(final String errorMessage) {
      this.errorMessage = errorMessage;
      return this;
    }
  }
}
