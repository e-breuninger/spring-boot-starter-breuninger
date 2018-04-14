package com.breuninger.boot.status.controller;

import static java.util.Collections.emptyList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.breuninger.boot.annotations.Beta;
import com.breuninger.boot.status.domain.ExternalDependency;

import lombok.Getter;

@Beta
@Getter
@Component
public class ExternalDependencies {

  private final List<ExternalDependency> dependencies;

  public ExternalDependencies(@Autowired(required = false) final List<ExternalDependency> dependencies) {
    if (dependencies == null) {
      this.dependencies = emptyList();
    } else {
      this.dependencies = dependencies;
    }
  }
}
