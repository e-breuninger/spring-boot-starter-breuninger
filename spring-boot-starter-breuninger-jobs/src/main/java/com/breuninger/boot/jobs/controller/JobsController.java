package com.breuninger.boot.jobs.controller;

import static java.util.stream.Collectors.toList;

import static javax.servlet.http.HttpServletResponse.SC_CONFLICT;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import static com.breuninger.boot.jobs.controller.JobRepresentation.representationOf;
import static com.breuninger.boot.util.UrlHelper.baseUriOf;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.breuninger.boot.configuration.BreuningerApplicationProperties;
import com.breuninger.boot.jobs.domain.JobInfo;
import com.breuninger.boot.jobs.domain.JobMeta;
import com.breuninger.boot.jobs.service.JobMetaService;
import com.breuninger.boot.jobs.service.JobService;
import com.breuninger.boot.navigation.NavBar;
import com.breuninger.boot.navigation.NavBarItem;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@ConditionalOnProperty(prefix = "breuninger.jobs", name = "external-trigger", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(BreuningerApplicationProperties.class)
public class JobsController {

  private final JobService jobService;
  private final JobMetaService jobMetaService;
  private final BreuningerApplicationProperties applicationProperties;

  JobsController(final JobService jobService, final JobMetaService jobMetaService, final NavBar rightNavBar,
                 final BreuningerApplicationProperties applicationProperties) {
    this.jobService = jobService;
    this.jobMetaService = jobMetaService;
    this.applicationProperties = applicationProperties;
    rightNavBar.register(
      NavBarItem.navBarItem(10, "Job Overview", applicationProperties.getManagement().getBasePath() + "/jobs"));
  }

  @RequestMapping(value = "${breuninger.application.management.base-path:/internal}/jobs", method = GET,
                  produces = "text/html")
  public ModelAndView getJobsAsHtml(@RequestParam(value = "type", required = false) final String type,
                                    @RequestParam(value = "count", defaultValue = "100") final int count,
                                    @RequestParam(value = "distinct", defaultValue = "true", required = false)
                                    final boolean distinct, final HttpServletRequest request) {
    final var jobRepresentations = getJobInfos(type, count, distinct).stream()
      .map(j -> representationOf(j, getJobMeta(j.getJobType()), true, baseUriOf(request),
        applicationProperties.getManagement().getBasePath()))
      .collect(toList());

    final var modelAndView = new ModelAndView("jobs");
    modelAndView.addObject("jobs", jobRepresentations);
    if (type != null) {
      modelAndView.addObject("typeFilter", type);
    }
    modelAndView.addObject("baseUri", baseUriOf(request));
    return modelAndView;
  }

  @RequestMapping(value = "${breuninger.application.management.base-path:/internal}/jobs", method = GET,
                  produces = "application/json")
  @ResponseBody
  public List<JobRepresentation> getJobsAsJson(@RequestParam(name = "type", required = false) final String type,
                                               @RequestParam(name = "count", defaultValue = "10") final int count,
                                               @RequestParam(name = "distinct", defaultValue = "true", required = false)
                                               final boolean distinct,
                                               @RequestParam(name = "humanReadable", defaultValue = "false", required = false)
                                               final boolean humanReadable, final HttpServletRequest request) {
    return getJobInfos(type, count, distinct).stream()
      .map(j -> representationOf(j, getJobMeta(j.getJobType()), humanReadable, baseUriOf(request),
        applicationProperties.getManagement().getBasePath()))
      .collect(toList());
  }

  @RequestMapping(value = "${breuninger.application.management.base-path:/internal}/jobs", method = DELETE)
  public void deleteJobs(@RequestParam(value = "type", required = false) final String type) {
    jobService.deleteJobs(Optional.ofNullable(type));
  }

  @RequestMapping(value = "${breuninger.application.management.base-path:/internal}/jobs/{jobType}", method = POST)
  public void startJob(@PathVariable final String jobType, final HttpServletRequest request, final HttpServletResponse response)
    throws IOException {
    final var jobId = jobService.startAsyncJob(jobType);
    if (jobId.isPresent()) {
      response.setHeader("Location",
        String.format("%s%s/jobs/%s", baseUriOf(request), applicationProperties.getManagement().getBasePath(), jobId.get()));
      response.setStatus(SC_NO_CONTENT);
    } else {
      response.sendError(SC_CONFLICT);
    }
  }

  @RequestMapping(value = "${breuninger.application.management.base-path:/internal}/jobs/{jobType}/disable", method = POST)
  public String disableJobType(@PathVariable final String jobType, @RequestParam(required = false) final String disabledComment) {
    jobMetaService.disable(jobType, disabledComment);
    return String.format("redirect:%s/jobdefinitions", applicationProperties.getManagement().getBasePath());
  }

  @RequestMapping(value = "${breuninger.application.management.base-path:/internal}/jobs/{jobType}/enable", method = POST)
  public String enableJobType(@PathVariable final String jobType) {
    jobMetaService.enable(jobType);
    return "redirect:" + applicationProperties.getManagement().getBasePath() + "/jobdefinitions";
  }

  @RequestMapping(value = "${breuninger.application.management.base-path:/internal}/jobs/{id}", method = GET,
                  produces = "text/html")
  public ModelAndView getJobAsHtml(final HttpServletRequest request, final HttpServletResponse response,
                                   @PathVariable("id") final String jobId) throws IOException {

    setCorsHeaders(response);

    final var optionalJob = jobService.findJob(jobId);
    if (optionalJob.isPresent()) {
      final var jobInfo = optionalJob.get();
      final var jobMeta = getJobMeta(jobInfo.getJobType());
      final var modelAndView = new ModelAndView("job");
      modelAndView.addObject("job",
        representationOf(jobInfo, jobMeta, true, baseUriOf(request), applicationProperties.getManagement().getBasePath()))
        .addObject("baseUri", baseUriOf(request));
      return modelAndView;
    } else {
      response.sendError(SC_NOT_FOUND, "Job not found");
      return null;
    }
  }

  @RequestMapping(value = "${breuninger.application.management.base-path:/internal}/jobs/{id}", method = GET,
                  produces = "application/json")
  @ResponseBody
  public JobRepresentation getJob(final HttpServletRequest request, final HttpServletResponse response,
                                  @PathVariable("id") final String jobId,
                                  @RequestParam(name = "humanReadable", defaultValue = "false", required = false)
                                  final boolean humanReadable) throws IOException {

    setCorsHeaders(response);

    final var optionalJob = jobService.findJob(jobId);
    if (optionalJob.isPresent()) {
      final var jobInfo = optionalJob.get();
      return representationOf(optionalJob.get(), getJobMeta(jobInfo.getJobType()), humanReadable, baseUriOf(request),
        applicationProperties.getManagement().getBasePath());
    } else {
      response.sendError(SC_NOT_FOUND, "Job not found");
      return null;
    }
  }

  private void setCorsHeaders(final HttpServletResponse response) {
    response.setHeader("Access-Control-Allow-Methods", "GET");
    response.setHeader("Access-Control-Allow-Origin", "*");
  }

  private JobMeta getJobMeta(final String jobType) {
    if (jobMetaService != null) {
      return jobMetaService.getJobMeta(jobType);
    } else {
      return null;
    }
  }

  private List<JobInfo> getJobInfos(final String type, final int count, final boolean distinct) {
    if (type == null && distinct) {
      return jobService.findJobsDistinct();
    } else {
      return jobService.findJobs(Optional.ofNullable(type), count);
    }
  }
}
