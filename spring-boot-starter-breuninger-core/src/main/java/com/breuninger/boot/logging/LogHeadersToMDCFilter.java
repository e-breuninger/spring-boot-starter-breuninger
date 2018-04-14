package com.breuninger.boot.logging;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

@Order
public class LogHeadersToMDCFilter extends OncePerRequestFilter {

  private final List<String> headerNames;

  public LogHeadersToMDCFilter(final List<String> headerNames) {
    this.headerNames = headerNames;
  }

  @Override
  protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
    throws ServletException, IOException {
    try {
      addHeaders(headerNames, request);
      filterChain.doFilter(request, response);
    } finally {
      removeHeaders(headerNames);
    }
  }

  private void removeHeaders(final List<String> headerNames) {
    headerNames.forEach(MDC::remove);
  }

  private void addHeaders(final List<String> headerNames, final HttpServletRequest request) {
    headerNames.forEach(headerName -> {
      final var value = request.getHeader(headerName);
      if (value != null) {
        MDC.put(headerName, value);
      }
    });
  }
}
