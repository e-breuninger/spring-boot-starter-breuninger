package com.breuninger.boot.metrics.http;

import static org.assertj.core.api.Assertions.assertThat;

import static com.breuninger.boot.acceptance.api.StatusApi.internal_status_is_retrieved_as;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import com.breuninger.boot.acceptance.api.StatusApi;

public class MetricsIntegrationTest {

  private MetricRegistry metricRegistry;

  @Before
  public void setUp() {
    metricRegistry = StatusApi.applicationContext().getBean(MetricRegistry.class);
  }

  @Test
  public void shouldReportHttpCountToGraphite() {
    final var counterBefore = Optional.ofNullable(metricRegistry.getCounters().get("counter.http.get.200"))
      .orElse(new Counter())
      .getCount();

    //when
    internal_status_is_retrieved_as("text/html");

    //then
    final var counterAfter = metricRegistry.getCounters().get("counter.http.get.200").getCount();
    assertThat(counterAfter - counterBefore).isEqualTo(1);
  }

  @Test
  public void shouldReportHttpTimeToGraphite() {
    final long timerSnapshotSizeBefore = Optional.ofNullable(metricRegistry.getTimers().get("timer.http.get"))
      .orElse(new Timer())
      .getSnapshot()
      .size();

    //when
    internal_status_is_retrieved_as("text/html");

    //then
    final long timerSnapshotSizeAfter = metricRegistry.getTimers().get("timer.http.get").getSnapshot().size();
    assertThat(timerSnapshotSizeAfter - timerSnapshotSizeBefore).isEqualTo(1);
  }
}
