package com.breuninger.boot.jobs.status;

import com.breuninger.boot.jobs.definition.JobDefinition;
import com.breuninger.boot.status.domain.StatusDetail;
import com.breuninger.boot.status.indicator.StatusDetailIndicator;

public class JobStatusDetailIndicator implements StatusDetailIndicator {

  private final JobStatusCalculator jobStatusCalculator;
  private final JobDefinition jobDefinition;

  public JobStatusDetailIndicator(final JobDefinition jobDefinition, final JobStatusCalculator jobStatusCalculator) {
    this.jobDefinition = jobDefinition;
    this.jobStatusCalculator = jobStatusCalculator;
  }

  @Override
  public StatusDetail statusDetail() {
    return jobStatusCalculator.statusDetail(jobDefinition);
  }
}
