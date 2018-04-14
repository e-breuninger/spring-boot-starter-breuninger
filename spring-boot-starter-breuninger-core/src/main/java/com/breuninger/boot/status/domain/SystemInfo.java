package com.breuninger.boot.status.domain;

import static java.time.Duration.between;
import static java.time.OffsetDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.jcip.annotations.Immutable;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@Immutable
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SystemInfo {

  private static final OffsetDateTime START_TIME = now();

  public final String hostname;
  public final int port;

  private SystemInfo(final String hostname, final int port) {
    this.hostname = hostname;
    this.port = port;
  }

  public static SystemInfo systemInfo(final String hostname, final int port) {
    return new SystemInfo(hostname, port);
  }

  public String getHostname() {
    return hostname;
  }

  public String getSystemTime() {
    return now().format(ISO_DATE_TIME);
  }

  public String getSystemStartTime() {
    return START_TIME.format(ISO_DATE_TIME);
  }

  public String getSystemUpTime() {
    final var seconds = between(START_TIME, now()).getSeconds();
    return String.format("%d:%02d:%02d", seconds / 3600, seconds % 3600 / 60, seconds % 60);
  }
}
