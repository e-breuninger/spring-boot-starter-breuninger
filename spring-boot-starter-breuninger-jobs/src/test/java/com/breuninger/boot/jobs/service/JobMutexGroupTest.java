package com.breuninger.boot.jobs.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import org.junit.Test;

public class JobMutexGroupTest {

  @Test
  public void shouldAddAllJobTypes() {
    final var group = new JobMutexGroup("Product Import Jobs", "FullImport", "DeltaImport");
    assertThat(group.getJobTypes(), contains("FullImport", "DeltaImport"));
  }
}
