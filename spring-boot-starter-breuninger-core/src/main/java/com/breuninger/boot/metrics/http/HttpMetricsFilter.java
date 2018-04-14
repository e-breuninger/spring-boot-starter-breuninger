package com.breuninger.boot.metrics.http;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.codahale.metrics.MetricRegistry;

@Component
public class HttpMetricsFilter implements Filter {

  private final MetricRegistry metricRegistry;

  public HttpMetricsFilter(final MetricRegistry metricRegistry) {
    this.metricRegistry = metricRegistry;
  }

  @Override
  public void init(final FilterConfig filterConfig) {
  }

  @Override
  public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
    throws IOException, ServletException {
    final var method = ((HttpServletRequest) request).getMethod().toLowerCase();
    final var context = metricRegistry.timer("timer.http." + method).time();
    try {
      chain.doFilter(request, response);
    } finally {
      if (response != null) {
        final var status = ((HttpServletResponse) response).getStatus();
        metricRegistry.counter("counter.http." + method + "." + status).inc();
        context.stop();
      }
    }
  }

  @Override
  public void destroy() {
  }
}
