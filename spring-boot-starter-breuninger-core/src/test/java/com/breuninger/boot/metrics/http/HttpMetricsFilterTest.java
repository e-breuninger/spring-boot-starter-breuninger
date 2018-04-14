package com.breuninger.boot.metrics.http;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;

public class HttpMetricsFilterTest {

  @Test
  public void shouldCountRequestsAndMeasureResponseTimes() throws IOException, ServletException {
    final var counter = mock(Counter.class);
    final var timer = mock(Timer.class);
    final var context = mock(Context.class);
    final var metricRegistry = mock(MetricRegistry.class);
    when(metricRegistry.counter(anyString())).thenReturn(counter);
    when(metricRegistry.timer(anyString())).thenReturn(timer);
    when(timer.time()).thenReturn(context);
    final var request = new MockHttpServletRequest("GET", "/foo/bar");
    final var response = mock(HttpServletResponse.class);
    when(response.getStatus()).thenReturn(200);

    // when
    new HttpMetricsFilter(metricRegistry).doFilter(request, response, mock(FilterChain.class));

    // then
    verify(metricRegistry).counter("counter.http.get.200");
    verify(counter).inc();
    verify(metricRegistry).timer("timer.http.get");
    verify(context).stop();
  }

  @Test
  public void shouldProceedInFilterChain() throws IOException, ServletException {
    final var metricRegistry = mock(MetricRegistry.class);
    when(metricRegistry.counter(anyString())).thenReturn(mock(Counter.class));
    final var timer = mock(Timer.class);
    final var context = mock(Context.class);
    when(metricRegistry.timer(anyString())).thenReturn(timer);
    when(timer.time()).thenReturn(context);
    final var filterChain = mock(FilterChain.class);
    final var request = mock(HttpServletRequest.class);
    when(request.getMethod()).thenReturn("GET");
    final var response = mock(HttpServletResponse.class);

    // when
    new HttpMetricsFilter(metricRegistry).doFilter(request, response, filterChain);

    // then
    verify(filterChain).doFilter(request, response);
  }
}
