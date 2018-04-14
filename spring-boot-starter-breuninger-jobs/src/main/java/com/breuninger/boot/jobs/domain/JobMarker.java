package com.breuninger.boot.jobs.domain;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class JobMarker {

  public static final Marker JOB = MarkerFactory.getMarker("JOB");

  public static Marker jobMarker(final String jobType) {
    final var marker = MarkerFactory.getMarker(jobType);
    JOB.add(marker);
    return marker;
  }
}
