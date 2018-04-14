package com.breuninger.boot.togglz.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.breuninger.boot.togglz.FeatureClassProvider;

@RestController
public class FeatureTogglesController {

  private final FeatureClassProvider featureClassProvider;

  public FeatureTogglesController(final FeatureClassProvider featureClassProvider) {
    this.featureClassProvider = featureClassProvider;
  }

  @RequestMapping(value = "${breuninger.application.management.base-path:/internal}/toggles", produces = {
    "application/vnd.breuninger.monitoring.status+json", "application/json"
  }, method = GET)
  public FeatureTogglesRepresentation getStatusAsJson() {
    return FeatureTogglesRepresentation.togglzRepresentation(featureClassProvider);
  }
}
