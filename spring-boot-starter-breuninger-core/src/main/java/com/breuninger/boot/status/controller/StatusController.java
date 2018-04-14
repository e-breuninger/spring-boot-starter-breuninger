package com.breuninger.boot.status.controller;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.breuninger.boot.status.controller.StatusRepresentation.DependencyRepresentation;
import com.breuninger.boot.status.domain.Criticality;
import com.breuninger.boot.status.domain.ExternalDependency;
import com.breuninger.boot.status.indicator.ApplicationStatusAggregator;

@RestController
public class StatusController {

  private final ApplicationStatusAggregator aggregator;
  private final ExternalDependencies externalDependencies;
  private final Criticality criticality;

  public StatusController(final ApplicationStatusAggregator aggregator, final ExternalDependencies externalDependencies, @Autowired(required = false) final Criticality criticality) {
    this.aggregator = aggregator;
    this.externalDependencies = externalDependencies;
    this.criticality = criticality;
  }

  @RequestMapping(value = "${breuninger.application.management.base-path:/internal}/status", produces = {
    "application/hal+json", "application/vnd.breuninger.monitoring.status+json", "application/json"
  }, method = GET)
  public StatusRepresentation getStatusAsJson() {
    return StatusRepresentation.statusRepresentationOf(aggregator.aggregatedStatus(), criticality, externalDependencies.getDependencies());
  }

  @RequestMapping(value = "${breuninger.application.management.base-path:/internal}/status", produces = "text/html", method = GET)
  public ModelAndView getStatusAsHtml() {
    return new ModelAndView("status", new HashMap() {{
      put("status", aggregator.aggregatedStatus());
      put("criticality", criticality);
      put("dependencies", externalDependencies.getDependencies()
        .stream()
        .sorted(comparing(ExternalDependency::getType).thenComparing(ExternalDependency::getName))
        .map(DependencyRepresentation::new)
        .collect(toList()));
    }});
  }
}

