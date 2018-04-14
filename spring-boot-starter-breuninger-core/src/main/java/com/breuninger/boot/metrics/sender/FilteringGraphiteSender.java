package com.breuninger.boot.metrics.sender;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.codahale.metrics.graphite.GraphiteSender;

public class FilteringGraphiteSender implements GraphiteSender {
  private static final Predicate<String> PREDICATE_NONE = a -> false;

  private final GraphiteSender delegate;
  private final Predicate<String> predicate;

  public FilteringGraphiteSender(final GraphiteSender delegate, final Predicate<String> predicate) {
    this.delegate = delegate;
    this.predicate = predicate;
  }

  public static Predicate<String> keepNone() {
    return PREDICATE_NONE;
  }

  public static Predicate<String> keepValuesByPattern(final Pattern pattern) {
    return pattern.asPredicate();
  }

  public static Predicate<String> keepValuesByPatterns(final Stream<Pattern> pattern) {
    return pattern.map(Pattern::asPredicate).reduce(keepNone(), Predicate::or);
  }

  public static Predicate<String> removePostfixValues(final String... postfixValues) {
    final var patternStream = Arrays.stream(postfixValues)
      .map(Pattern::quote)
      .map(s -> ".*" + s + "$")
      .map(Pattern::compile);
    return keepValuesByPatterns(patternStream).negate();
  }

  @Override
  public void connect() throws IllegalStateException, IOException {
    delegate.connect();
  }

  @Override
  public void send(final String name, final String value, final long timestamp) throws IOException {
    if (predicate.test(name)) {
      delegate.send(name, value, timestamp);
    }
  }

  @Override
  public void flush() throws IOException {
    delegate.flush();
  }

  @Override
  public boolean isConnected() {
    return delegate.isConnected();
  }

  @Override
  public int getFailures() {
    return delegate.getFailures();
  }

  @Override
  public void close() throws IOException {
    delegate.close();
  }
}
