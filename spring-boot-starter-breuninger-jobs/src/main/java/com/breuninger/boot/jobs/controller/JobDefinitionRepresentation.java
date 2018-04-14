package com.breuninger.boot.jobs.controller;

import static java.util.Arrays.asList;

import static com.breuninger.boot.status.domain.Link.link;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import com.breuninger.boot.jobs.definition.JobDefinition;
import com.breuninger.boot.status.domain.Link;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@JsonInclude(Include.NON_NULL)
public class JobDefinitionRepresentation {

  public final String type;
  public final String name;
  public final int retries;
  public final Long retryDelay;
  public final String cron;
  public final Long maxAge;
  public final Long fixedDelay;
  public final List<Link> links;

  private JobDefinitionRepresentation(final JobDefinition jobDefinition, final String baseUri,
                                      final String breuningerManagementBasePath) {
    type = jobDefinition.jobType();
    name = jobDefinition.jobName();
    retries = jobDefinition.retries();
    retryDelay = valueOf(jobDefinition.retryDelay());
    cron = jobDefinition.cron().orElse(null);
    maxAge = valueOf(jobDefinition.maxAge());
    fixedDelay = valueOf(jobDefinition.fixedDelay());
    links = linksOf(jobDefinition, baseUri, breuningerManagementBasePath);
  }

  public static JobDefinitionRepresentation representationOf(final JobDefinition jobDefinition, final String baseUri,
                                                             final String breuningerManagementBasePath) {
    return new JobDefinitionRepresentation(jobDefinition, baseUri, breuningerManagementBasePath);
  }

  private List<Link> linksOf(final JobDefinition jobDefinition, final String baseUri, final String breuningerManagementBasePath) {
    return asList(
      link("self", String.format("%s%s/jobsdefinitions/%s", baseUri, breuningerManagementBasePath, jobDefinition.jobType()), null),
      link("collection", String.format("%s%s/jobdefinitions", baseUri, breuningerManagementBasePath), null),
      link("http://github.com/e-breuninger/spring-boot-starter-breuninger/link-relations/job/trigger",
        String.format("%s%s/jobs/%s", baseUri, breuningerManagementBasePath, jobDefinition.jobType()), null));
  }

  private Long valueOf(final Optional<Duration> duration) {
    return duration.map(Duration::getSeconds).orElse(null);
  }
}
