package com.breuninger.boot.jobs.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RunningJob {

  public final String jobId;
  public final String jobType;
}
