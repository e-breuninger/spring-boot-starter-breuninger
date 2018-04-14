package com.breuninger.boot.status.domain;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.breuninger.boot.status.controller.StatusController;
import com.breuninger.boot.status.indicator.ApplicationStatusAggregator;
import com.breuninger.boot.status.indicator.StatusDetailIndicator;

import net.jcip.annotations.Immutable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Immutable
@Getter
@EqualsAndHashCode
@ToString
public class StatusDetail {

  private final String name;
  private final Status status;
  private final String message;
  private final List<Link> links;
  private final Map<String, String> details;

  private StatusDetail(final String name, final Status status, final String message, final List<Link> links,
                       final Map<String, String> details) {
    this.name = name;
    this.status = status;
    this.message = message;
    this.links = unmodifiableList(links);
    this.details = unmodifiableMap(new LinkedHashMap<>(details));
  }

  public static StatusDetail statusDetail(final String name, final Status status, final String message) {
    return new StatusDetail(name, status, message, emptyList(), emptyMap());
  }

  public static StatusDetail statusDetail(final String name, final Status status, final String message,
                                          final Map<String, String> additionalAttributes) {
    return new StatusDetail(name, status, message, emptyList(), additionalAttributes);
  }

  public static StatusDetail statusDetail(final String name, final Status status, final String message, final Link link) {
    return new StatusDetail(name, status, message, singletonList(link), emptyMap());
  }

  public static StatusDetail statusDetail(final String name, final Status status, final String message, final List<Link> links) {
    return new StatusDetail(name, status, message, links, emptyMap());
  }

  public static StatusDetail statusDetail(final String name, final Status status, final String message, final Link link,
                                          final Map<String, String> additionalAttributes) {
    return new StatusDetail(name, status, message, singletonList(link), additionalAttributes);
  }

  public static StatusDetail statusDetail(final String name, final Status status, final String message, final List<Link> links,
                                          final Map<String, String> additionalAttributes) {
    return new StatusDetail(name, status, message, links, additionalAttributes);
  }

  public StatusDetail toOk(final String message) {
    return statusDetail(name, Status.OK, message, details);
  }

  public StatusDetail toWarning(final String message) {
    return statusDetail(name, Status.WARNING, message, details);
  }

  public StatusDetail toError(final String message) {
    return statusDetail(name, Status.ERROR, message, details);
  }

  public StatusDetail withDetail(final String key, final String value) {
    final LinkedHashMap<String, String> newDetails = new LinkedHashMap<>(details);
    newDetails.put(key, value);
    return statusDetail(name, status, message, newDetails);
  }

  public StatusDetail withoutDetail(final String key) {
    final LinkedHashMap<String, String> newDetails = new LinkedHashMap<>(details);
    newDetails.remove(key);
    return statusDetail(name, status, message, newDetails);
  }
}
