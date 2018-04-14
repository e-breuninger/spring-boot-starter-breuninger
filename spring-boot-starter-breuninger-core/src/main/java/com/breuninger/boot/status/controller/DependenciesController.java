package com.breuninger.boot.status.controller;

import static java.util.Collections.singletonMap;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.breuninger.boot.annotations.Beta;

@Beta
@RestController
public class DependenciesController {

  private final ExternalDependencies externalDependencies;

  public DependenciesController(final ExternalDependencies externalDependencies) {
    this.externalDependencies = externalDependencies;
  }

  @GetMapping(value = "${breuninger.application.management.base-path:/internal}/dependencies", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, List<?>>> listDependencies() {
    final Map<String, List<?>> dependencies = singletonMap("dependencies", externalDependencies.getDependencies());
    return ok(dependencies);
  }
}
