package com.breuninger.boot.status.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.breuninger.boot.configuration.BreuningerApplicationProperties;

@Controller
@ConditionalOnProperty(name = "breuninger.status.redirect-internal.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({ServerProperties.class, BreuningerApplicationProperties.class})
public class InternalController {

  private final BreuningerApplicationProperties properties;
  private final ServerProperties serverProperties;

  public InternalController(final BreuningerApplicationProperties properties, final ServerProperties serverProperties) {
    this.properties = properties;
    this.serverProperties = serverProperties;
  }

  @RequestMapping("${breuninger.application.management.base-path:/internal}")
  public void redirectToStatus(final HttpServletResponse response) throws IOException {
    response.sendRedirect(
      String.format("%s%s/status", serverProperties.getServlet().getContextPath(), properties.getManagement().getBasePath()));
  }
}
