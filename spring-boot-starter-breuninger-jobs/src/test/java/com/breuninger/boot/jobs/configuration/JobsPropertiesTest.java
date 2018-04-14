package com.breuninger.boot.jobs.configuration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;

import java.util.HashMap;

import org.junit.Test;

public class JobsPropertiesTest {

  @Test
  public void shouldNormalizeJobTypes() {
    // given
    final var jobsProperties = new JobsProperties();
    jobsProperties.getStatus().setCalculator(new HashMap() {{
      put("Some Job Type", "foo");
    }});
    // when
    final var calculators = jobsProperties.getStatus().getCalculator();
    // then
    assertThat(calculators, hasEntry("some-job-type", "foo"));
  }
}
