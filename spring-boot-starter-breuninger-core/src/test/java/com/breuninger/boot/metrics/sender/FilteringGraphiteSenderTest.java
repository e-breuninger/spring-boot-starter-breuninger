package com.breuninger.boot.metrics.sender;

import static java.util.regex.Pattern.compile;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import static com.breuninger.boot.metrics.sender.FilteringGraphiteSender.keepValuesByPattern;
import static com.breuninger.boot.metrics.sender.FilteringGraphiteSender.removePostfixValues;

import java.io.IOException;
import java.util.function.Predicate;

import org.junit.Test;

import com.codahale.metrics.graphite.GraphiteSender;

import com.breuninger.boot.metrics.configuration.GraphiteReporterConfiguration;

public class FilteringGraphiteSenderTest {
  private final Long timestamp = 2L;
  private final String value = "45";
  private final Predicate<String> predicate = keepValuesByPattern(compile("anothermetric"));
  private final GraphiteSender delegate = mock(GraphiteSender.class);

  @Test
  public void shouldSendMetricForNotFilteredSuffix() throws Exception {
    // given
    final var name = "testtest.foo.bar.metrics.filedescriptors.ratio";

    // when
    new FilteringGraphiteSender(delegate, new GraphiteReporterConfiguration().graphiteFilterPredicate()).send(name, value,
      timestamp);

    // then
    verify(delegate).send(name, value, timestamp);
  }

  @Test
  public void shouldNotSendMetricForFilteredSuffixes() throws Exception {
    // given
    final var name = "testtest.foo.bar.metrics.filedescriptors.p98";

    // when
    new FilteringGraphiteSender(delegate, new GraphiteReporterConfiguration().graphiteFilterPredicate()).send(name, value,
      timestamp);

    // then
    verifyZeroInteractions(delegate);
  }

  @Test
  public void shouldNotSendMetric() throws Exception {
    // given
    final var name = "metrics.http.exception.p95";

    // when
    sendValue(name, delegate);

    // then
    verifyZeroInteractions(delegate);
  }

  @Test
  public void shouldSendMetric() throws Exception {
    // given
    final var name = "metrics.anothermetric.min";

    // when
    sendValue(name, delegate);

    // then
    verify(delegate).send(name, value, timestamp);
    verifyNoMoreInteractions(delegate);
  }

  @Test
  public void shouldFilterDefaultValues() throws Exception {
    // given
    final var graphiteSender = new FilteringGraphiteSender(delegate, removePostfixValues("abc"));
    final var name = "holy.moly.string.ends.with.abc";

    // when
    graphiteSender.send(name, value, timestamp);

    // then
    verifyZeroInteractions(delegate);
  }

  @Test
  public void shouldNotFilterNonDefaultValues() throws Exception {
    // given
    final var graphiteSender = new FilteringGraphiteSender(delegate, removePostfixValues("abc"));
    final var name = "holy.moly.string.ends.with.def";

    // when
    graphiteSender.send(name, value, timestamp);

    // then
    verify(delegate).send(name, value, timestamp);
  }

  private void sendValue(final String name, final GraphiteSender delegate) throws IOException {
    new FilteringGraphiteSender(delegate, predicate).send(name, value, timestamp);
  }
}
