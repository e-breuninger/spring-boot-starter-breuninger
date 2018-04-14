package com.breuninger.boot.jobs.domain;

import static java.util.Collections.unmodifiableMap;

import java.util.Map;

import lombok.Getter;

@Getter
public final class JobMeta {

  private final String jobType;
  private final boolean running;
  private final boolean disabled;
  private final String disabledComment;
  private final Map<String, String> meta;

  public JobMeta(final String jobType, final boolean running, final boolean disabled, final String disabledComment,
                 final Map<String, String> meta) {
    this.jobType = jobType;
    this.running = running;
    this.disabled = disabled;
    this.disabledComment = disabledComment != null ? disabledComment : "";
    this.meta = unmodifiableMap(meta);
  }

  public String get(final String key) {
    return meta.get(key);
  }

  public Map<String, String> getAll() {
    return meta;
  }
}
