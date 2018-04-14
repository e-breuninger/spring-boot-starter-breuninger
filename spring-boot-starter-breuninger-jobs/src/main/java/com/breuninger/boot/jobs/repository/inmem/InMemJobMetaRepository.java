package com.breuninger.boot.jobs.repository.inmem;

import static java.util.Collections.emptyMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.breuninger.boot.jobs.domain.JobMeta;
import com.breuninger.boot.jobs.repository.JobMetaRepository;

public class InMemJobMetaRepository implements JobMetaRepository {

  private static final String KEY_DISABLED = "_e_disabled";
  private static final String KEY_RUNNING = "_e_running";
  private final Map<String, Map<String, String>> map = new ConcurrentHashMap<>();

  @Override
  public JobMeta getJobMeta(final String jobType) {
    final var document = map.get(jobType);
    if (document != null) {
      final var meta = document.keySet()
        .stream()
        .filter(key -> !key.startsWith("_e_"))
        .collect(toMap(identity(), document::get));
      final var isRunning = document.containsKey(KEY_RUNNING);
      final var isDisabled = document.containsKey(KEY_DISABLED);
      final var comment = document.get(KEY_DISABLED);
      return new JobMeta(jobType, isRunning, isDisabled, comment, meta);
    } else {
      return new JobMeta(jobType, false, false, "", emptyMap());
    }
  }

  @Override
  public boolean createValue(final String jobType, final String key, final String value) {
    if (getValue(jobType, key) == null) {
      setValue(jobType, key, value);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean setRunningJob(final String jobType, final String jobId) {
    return createValue(jobType, KEY_RUNNING, jobId);
  }

  @Override
  public String getRunningJob(final String jobType) {
    return getValue(jobType, KEY_RUNNING);
  }

  @Override
  public void clearRunningJob(final String jobType) {
    setValue(jobType, KEY_RUNNING, null);
  }

  @Override
  public void disable(final String jobType, final String comment) {
    setValue(jobType, KEY_DISABLED, comment != null ? comment : "");
  }

  @Override
  public void enable(final String jobType) {
    setValue(jobType, KEY_DISABLED, null);
  }

  @Override
  public String setValue(final String jobType, final String key, final String value) {
    map.putIfAbsent(jobType, new ConcurrentHashMap<>());
    if (value != null) {
      return map.get(jobType).put(key, value);
    } else {
      return map.get(jobType).remove(key);
    }
  }

  @Override
  public String getValue(final String jobType, final String key) {
    return map.getOrDefault(jobType, emptyMap()).get(key);
  }

  @Override
  public Set<String> findAllJobTypes() {
    return map.keySet();
  }

  @Override
  public void deleteAll() {
    map.clear();
  }

  @Override
  public String toString() {
    return "InMemJobMetaRepository";
  }
}
