package com.breuninger.boot.logging.ui;

import static java.util.stream.Collectors.toList;

import static org.springframework.boot.logging.LogLevel.valueOf;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import static com.breuninger.boot.util.UrlHelper.baseUriOf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.actuate.endpoint.http.ActuatorMediaType;
import org.springframework.boot.actuate.logging.LoggersEndpoint;
import org.springframework.boot.actuate.logging.LoggersEndpoint.LoggerLevels;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.breuninger.boot.configuration.BreuningerApplicationProperties;
import com.breuninger.boot.navigation.NavBar;
import com.breuninger.boot.navigation.NavBarItem;

@Controller
public class LoggersController {

  private final LoggersEndpoint loggersEndpoint;
  private final BreuningerApplicationProperties applicationProperties;

  public LoggersController(final LoggersEndpoint loggersEndpoint, final NavBar rightNavBar,
                           final BreuningerApplicationProperties applicationProperties) {
    this.loggersEndpoint = loggersEndpoint;
    this.applicationProperties = applicationProperties;
    rightNavBar.register(
      NavBarItem.navBarItem(1, "Loggers", String.format("%s/loggers", applicationProperties.getManagement().getBasePath())));
  }

  @RequestMapping(value = "${breuninger.application.management.base-path:/internal}/loggers", produces = {
    TEXT_HTML_VALUE, ALL_VALUE
  }, method = GET)
  public ModelAndView get(final HttpServletRequest request) {
    return new ModelAndView("loggers", new HashMap() {{
      put("loggers", getLoggers());
      put("baseUri", baseUriOf(request));
    }});
  }

  @RequestMapping(value = "${breuninger.application.management.base-path:/internal}/loggers", produces = {
    ActuatorMediaType.V2_JSON, APPLICATION_JSON_VALUE
  }, method = GET)
  @ResponseBody
  public Object get() {
    final var levels = loggersEndpoint.loggers();
    return levels == null ? notFound().build() : levels;
  }

  @RequestMapping(value = "${breuninger.application.management.base-path:/internal}/loggers/{name:.*}", produces = {
    ActuatorMediaType.V2_JSON, APPLICATION_JSON_VALUE
  }, method = GET)
  @ResponseBody
  public Object get(@PathVariable final String name) {
    final var levels = loggersEndpoint.loggerLevels(name);
    return levels == null ? notFound().build() : levels;
  }

  @RequestMapping(value = "${breuninger.application.management.base-path:/internal}/loggers",
                  consumes = APPLICATION_FORM_URLENCODED_VALUE, produces = TEXT_HTML_VALUE, method = POST)
  public RedirectView post(@ModelAttribute("name") final String name, @ModelAttribute("level") final String level,
                           final HttpServletRequest httpServletRequest) {
    final var logLevel = level == null ? null : valueOf(level.toUpperCase());
    loggersEndpoint.configureLogLevel(name, logLevel);
    return new RedirectView(
      String.format("%s%s/loggers", baseUriOf(httpServletRequest), applicationProperties.getManagement().getBasePath()));
  }

  @RequestMapping(value = "${breuninger.application.management.base-path:/internal}/loggers/{name:.*}", consumes = {
    ActuatorMediaType.V2_JSON, APPLICATION_JSON_VALUE
  }, produces = {
    ActuatorMediaType.V2_JSON, APPLICATION_JSON_VALUE
  }, method = POST)
  @ResponseBody
  public Object post(@PathVariable final String name, @RequestBody final Map<String, String> configuration) {
    final var level = configuration.get("configuredLevel");
    final var logLevel = level == null ? null : valueOf(level.toUpperCase());
    loggersEndpoint.configureLogLevel(name, logLevel);
    return HttpEntity.EMPTY;
  }

  private List<Map<String, ?>> getLoggers() {
    final Map<String, ?> loggers = (Map) loggersEndpoint.loggers().get("loggers");
    return loggers.keySet().stream().map(key -> key.contains("$") ? null : new HashMap<String, Object>() {{
      final var logger = (LoggerLevels) loggers.get(key);
      put("name", key);
      put("displayName", displayNameOf(key));
      put("configuredLevel", logger.getConfiguredLevel());
      put("effectiveLevel", logger.getEffectiveLevel());
    }}).filter(Objects::nonNull).collect(toList());
  }

  private String displayNameOf(final String key) {

    if (key.contains(".")) {
      final var tokenizer = new StringTokenizer(key, ".");
      final var joiner = new StringJoiner(".");
      var pos = 0;
      while (tokenizer.hasMoreTokens()) {
        final var word = tokenizer.nextToken();
        if (tokenizer.hasMoreTokens() && pos > 1) {
          joiner.add(word.substring(0, 1));
        } else {
          joiner.add(word);
        }
        ++pos;
      }
      return joiner.toString();
    } else {
      return key;
    }
  }
}
