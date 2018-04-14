package com.breuninger.boot.metrics.configuration;

import static java.lang.Integer.valueOf;
import static java.lang.String.join;
import static java.net.InetAddress.getLocalHost;
import static java.util.Arrays.asList;

import static com.codahale.metrics.graphite.GraphiteReporter.forRegistry;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.breuninger.boot.metrics.sender.FilteringGraphiteSender;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.graphite.GraphiteSender;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableConfigurationProperties(MetricsProperties.class)
@ConditionalOnProperty(prefix = "breuninger.metrics.graphite", name = {"host", "port", "prefix"})
public class GraphiteReporterConfiguration {

  private static String reverse(final String host) {
    final var parts = asList(host.split("\\."));
    Collections.reverse(parts);
    return join(".", parts);
  }

  private static String hostName() {

    final var envHost = System.getenv("HOST");
    if (envHost != null) {
      return envHost;
    }
    try {
      return getLocalHost().getCanonicalHostName();
    } catch (final UnknownHostException e) {
      final var msg = "Error resolving canonical name of localhost";
      LOG.error(msg, e);
      throw new RuntimeException(msg, e);
    }
  }

  @Bean
  public GraphiteReporter graphiteReporter(final MetricRegistry metricRegistry, final MetricsProperties metricsProperties,
                                           final Predicate<String> graphiteFilterPredicate) {
    final var graphiteMetricsProperties = metricsProperties.getGraphite();
    final var prefix = graphiteMetricsProperties.isAddHostToPrefix() ?
      graphiteMetricsProperties.getPrefix() + "." + reverse(hostName()) + ".metrics" :
      graphiteMetricsProperties.getPrefix();
    final var graphiteReporter = forRegistry(metricRegistry).prefixedWith(prefix)
      .build(graphiteSender(graphiteMetricsProperties, graphiteFilterPredicate));
    graphiteReporter.start(1, TimeUnit.MINUTES);
    return graphiteReporter;
  }

  private GraphiteSender graphiteSender(final MetricsProperties.Graphite graphiteMetricsProperties,
                                        final Predicate<String> graphiteFilterPredicate) {
    final var address = new InetSocketAddress(graphiteMetricsProperties.getHost(),
      valueOf(graphiteMetricsProperties.getPort()));
    return new FilteringGraphiteSender(new Graphite(address), graphiteFilterPredicate);
  }

  @Bean
  @ConditionalOnMissingBean
  public Predicate<String> graphiteFilterPredicate() {
    return FilteringGraphiteSender.removePostfixValues(".m5_rate", ".m15_rate", ".min", ".max", ".mean_rate", ".p50", ".p75",
      ".p98", ".stddev");
  }
}
