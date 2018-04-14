package com.breuninger.boot.metrics.configuration;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Configuration;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.CachedThreadStatesGaugeSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;

@Configuration
@EnableMetrics
public class MetricsJvmConfiguration extends MetricsConfigurerAdapter {

  @Override
  public void configureReporters(final MetricRegistry metricRegistry) {
    metricRegistry.register("gc", new GarbageCollectorMetricSet());
    metricRegistry.register("memory", new MemoryUsageGaugeSet());
    metricRegistry.register("filedescriptors.ratio", new FileDescriptorRatioGauge());
    metricRegistry.register("threads", new CachedThreadStatesGaugeSet(10, TimeUnit.SECONDS));
  }
}
