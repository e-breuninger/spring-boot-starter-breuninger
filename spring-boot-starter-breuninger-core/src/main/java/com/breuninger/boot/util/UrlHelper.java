package com.breuninger.boot.util;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentServletMapping;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

public class UrlHelper {

  private UrlHelper() {
  }

  public static String baseUriOf(final HttpServletRequest request) {
    final var requestUrl = request.getRequestURL();
    return requestUrl != null ? requestUrl.substring(0, requestUrl.indexOf(request.getServletPath())) : "";
  }

  public static String absoluteHrefOf(final String path) {
    try {
      return fromCurrentServletMapping().path(path).build().toString();
    } catch (final IllegalStateException e) {
      return path;
    }
  }

  public static URL url(final String url) {
    try {
      return new URL(url);
    } catch (final MalformedURLException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }
}
