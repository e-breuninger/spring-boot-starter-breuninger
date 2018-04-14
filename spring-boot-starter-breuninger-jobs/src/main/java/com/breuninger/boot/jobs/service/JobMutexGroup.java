package com.breuninger.boot.jobs.service;

import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class JobMutexGroup {

  private final String groupName;
  private final Set<String> jobTypes;

  public JobMutexGroup(final String groupName, final String jobType, final String... moreJobTypes) {
    this.groupName = groupName;
    jobTypes = new HashSet() {{
      add(jobType);
      addAll(asList(moreJobTypes));
    }};
  }
}
