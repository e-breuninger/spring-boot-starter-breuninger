package com.breuninger.boot.jobs.service;

import static java.util.Collections.emptySet;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobMutexGroups {

  private Set<JobMutexGroup> mutexGroups = emptySet();

  public Set<JobMutexGroup> getMutexGroups() {
    return mutexGroups;
  }

  @Autowired(required = false)
  public void setMutexGroups(final Set<JobMutexGroup> mutexGroups) {
    this.mutexGroups = mutexGroups;
  }

  public Set<String> mutexJobTypesFor(final String jobType) {
    final Set<String> result = new HashSet<>();
    mutexGroups.stream().map(JobMutexGroup::getJobTypes).filter(g -> g.contains(jobType)).forEach(result::addAll);
    result.remove(jobType);
    return result;
  }
}
