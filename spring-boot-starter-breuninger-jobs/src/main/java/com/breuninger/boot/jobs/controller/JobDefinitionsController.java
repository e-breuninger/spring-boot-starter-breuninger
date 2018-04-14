package com.breuninger.boot.jobs.controller;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import static com.breuninger.boot.status.domain.Link.link;
import static com.breuninger.boot.util.UrlHelper.baseUriOf;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.breuninger.boot.configuration.BreuningerApplicationProperties;
import com.breuninger.boot.jobs.definition.JobDefinition;
import com.breuninger.boot.jobs.service.JobDefinitionService;
import com.breuninger.boot.jobs.service.JobMetaService;
import com.breuninger.boot.navigation.NavBar;
import com.breuninger.boot.navigation.NavBarItem;
import com.breuninger.boot.status.domain.Link;

@Controller
@ConditionalOnProperty(prefix = "breuninger.jobs", name = "external-trigger", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(BreuningerApplicationProperties.class)
public class JobDefinitionsController {

  private final String jobDefinitionsUri;

  private final JobDefinitionService jobDefinitionService;
  private final JobMetaService jobMetaService;
  private final BreuningerApplicationProperties applicationProperties;

  public JobDefinitionsController(final JobDefinitionService definitionService, final JobMetaService jobMetaService,
                                  final NavBar rightNavBar, final BreuningerApplicationProperties applicationProperties) {
    jobDefinitionService = definitionService;
    this.jobMetaService = jobMetaService;
    this.applicationProperties = applicationProperties;
    jobDefinitionsUri = String.format("%s/jobdefinitions", applicationProperties.getManagement().getBasePath());
    rightNavBar.register(NavBarItem.navBarItem(10, "Job Definitions", jobDefinitionsUri));
  }

  @RequestMapping(value = "${breuninger.application.management.base-path:/internal}/jobdefinitions", method = GET,
                  produces = "application/json")
  @ResponseBody
  public Map<String, List<Link>> getJobDefinitionsAsJson(final HttpServletRequest request) {
    final var baseUri = baseUriOf(request);
    return singletonMap("links", new ArrayList() {{
      addAll(jobDefinitionService.getJobDefinitions()
        .stream()
        .map(def -> link("http://github.com/e-breuninger/spring-boot-starter-breuninger/link-relations/job/definition",
          baseUri + jobDefinitionsUri + "/" + def.jobType(), def.jobName()))
        .collect(toList()));
      add(link("self", baseUriOf(request) + jobDefinitionsUri, "Self"));
    }});
  }

  @RequestMapping(value = "${breuninger.application.management.base-path:/internal}/jobdefinitions", method = GET,
                  produces = "*/*")
  public ModelAndView getJobDefinitionsAsHtml(final HttpServletRequest request) {
    return new ModelAndView("jobdefinitions", new HashMap() {{
      put("baseUri", baseUriOf(request));
      put("jobdefinitions", jobDefinitionService.getJobDefinitions().stream().map(def -> {
        final var jobMeta = jobMetaService.getJobMeta(def.jobType());
        return new HashMap<String, Object>() {{
          put("isDisabled", jobMeta != null && jobMeta.isDisabled());
          put("comment", jobMeta != null ? jobMeta.getDisabledComment() : "");
          put("jobType", def.jobType());
          put("name", def.jobName());
          put("description", def.description());
          put("maxAge", def.maxAge().isPresent() ? def.maxAge().get().toMinutes() + " Minutes" : "unlimited");
          put("frequency", frequencyOf(def));
          put("retry", retryOf(def));
        }};
      }).collect(toList()));
    }});
  }

  @RequestMapping(value = "${breuninger.application.management.base-path:/internal}/jobdefinitions/{jobType}", method = GET,
                  produces = "application/json")
  @ResponseBody
  public JobDefinitionRepresentation getJobDefinition(@PathVariable final String jobType, final HttpServletRequest request,
                                                      final HttpServletResponse response) throws IOException {

    final var jobDefinition = jobDefinitionService.getJobDefinition(jobType);
    if (jobDefinition.isPresent()) {
      return JobDefinitionRepresentation.representationOf(jobDefinition.get(), baseUriOf(request),
        applicationProperties.getManagement().getBasePath());
    } else {
      response.sendError(SC_NOT_FOUND, "Job not found");
      return null;
    }
  }

  @RequestMapping(value = "${breuninger.application.management.base-path:/internal}/jobdefinitions/{jobType}", method = GET,
                  produces = "*/*")
  public ModelAndView getJobDefinitionAsHtml(@PathVariable final String jobType, final HttpServletRequest request,
                                             final HttpServletResponse response) throws IOException {
    final var jobMeta = jobMetaService.getJobMeta(jobType);
    final Optional<HashMap<String, Object>> optionalResult = jobDefinitionService.getJobDefinition(jobType)
      .map(def -> new HashMap() {{
        put("isDisabled", jobMeta.isDisabled());
        put("comment", jobMeta.getDisabledComment());
        put("jobType", def.jobType());
        put("name", def.jobName());
        put("description", def.description());
        put("maxAge", def.maxAge().isPresent() ? def.maxAge().get().toMinutes() + " Minutes" : "unlimited");
        put("frequency", frequencyOf(def));
        put("retry", retryOf(def));
      }});
    if (optionalResult.isPresent()) {
      return new ModelAndView("jobdefinitions", new HashMap() {{
        put("baseUri", baseUriOf(request));
        put("jobdefinitions", singletonList(optionalResult.get()));
      }});
    } else {
      response.sendError(SC_NOT_FOUND, "JobDefinition " + jobType + " not found.");
      return null;
    }
  }

  private String frequencyOf(final JobDefinition def) {
    if (def.cron().isPresent()) {
      return def.cron().get();
    } else {
      return fixedDelayFrequency(def.fixedDelay());
    }
  }

  private String fixedDelayFrequency(final Optional<Duration> duration) {
    if (duration.isPresent()) {
      if (duration.get().toMinutes() < 1) {
        return "Every " + duration.get().toMillis() / 1000 + " Seconds";
      } else {
        return "Every " + duration.get().toMinutes() + " Minutes";
      }
    } else {
      return "Never";
    }
  }

  private String retryOf(final JobDefinition def) {
    final var delay = def.retryDelay().isPresent() ? " with " + def.retryDelay().get().getSeconds() + " seconds delay." : ".";
    return def.retries() == 0 ? "Do not retry triggering" : "Retry trigger " + def.retries() + " times" + delay;
  }
}
