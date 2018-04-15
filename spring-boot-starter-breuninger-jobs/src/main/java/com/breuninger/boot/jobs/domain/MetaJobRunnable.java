package com.breuninger.boot.jobs.domain;

import static java.time.Instant.ofEpochMilli;

import java.time.Instant;

import com.breuninger.boot.jobs.repository.JobMetaRepository;
import com.breuninger.boot.jobs.service.JobRunnable;

public abstract class MetaJobRunnable implements JobRunnable {

  private final JobMetaRepository metaRepository;
  private final String jobType;

  protected MetaJobRunnable(final String jobType, final JobMetaRepository metaRepository) {

    this.jobType = jobType;
    this.metaRepository = metaRepository;
  }

  public final String getJobType() {
    return jobType;
  }

  public final void deleteMeta(final String key) {
    checkKey(key);
    metaRepository.setValue(jobType, key, null);
  }

  public final void setMeta(final String key, final String value) {
    checkKey(key);
    metaRepository.setValue(jobType, key, value);
  }

  public final void setMeta(final String key, final int value) {
    checkKey(key);
    metaRepository.setValue(jobType, key, String.valueOf(value));
  }

  public final void setMeta(final String key, final long value) {
    checkKey(key);
    metaRepository.setValue(jobType, key, String.valueOf(value));
  }

  public final void setMeta(final String key, final Instant value) {
    checkKey(key);
    setMeta(key, value.toEpochMilli());
  }

  public final String getMetaAsString(final String key) {
    checkKey(key);
    return getMetaAsString(key, null);
  }

  public final String getMetaAsString(final String key, final String defaultValue) {
    checkKey(key);
    final var value = metaRepository.getValue(jobType, key);
    return value != null ? value : defaultValue;
  }

  public int getMetaAsInt(final String key, final int defaultValue) {
    checkKey(key);
    final var value = getMetaAsString(key);
    return value != null ? Integer.valueOf(value) : defaultValue;
  }

  public final long getMetaAsLong(final String key, final long defaultValue) {
    checkKey(key);
    final var value = getMetaAsString(key);
    return value != null ? Long.valueOf(value) : defaultValue;
  }

  public final Instant getMetaAsInstant(final String key) {
    checkKey(key);
    return getMetaAsInstant(key, null);
  }

  public final Instant getMetaAsInstant(final String key, final Instant defaultValue) {
    checkKey(key);
    final var epochMilli = getMetaAsLong(key, -1);
    if (epochMilli != -1) {
      return ofEpochMilli(epochMilli);
    } else {
      return defaultValue;
    }
  }

  private void checkKey(final String key) {
    if (key == null || key.startsWith("_e_")) {
      throw new IllegalArgumentException("Keys must never be null and must not start with prefix _e_");
    }
  }
}
