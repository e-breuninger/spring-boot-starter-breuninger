package com.breuninger.boot.status.domain;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import java.util.List;

import com.breuninger.boot.annotations.Beta;

@Beta
public class ServiceDependencyBuilder {

  private String name;
  private String description;
  private String url;
  private String type;
  private String subtype;
  private List<String> methods;
  private List<String> mediaTypes;
  private String authentication;
  private Criticality criticality;
  private Expectations expectations;

  public static ServiceDependencyBuilder copyOf(final ServiceDependency prototype) {
    return new ServiceDependencyBuilder().withName(prototype.getName())
      .withDescription(prototype.getDescription())
      .withUrl(prototype.getUrl())
      .withType(prototype.getType())
      .withSubtype(prototype.getSubtype())
      .withMethods(prototype.getMethods())
      .withMediaTypes(prototype.getMediaTypes())
      .withAuthentication(prototype.getAuthentication())
      .withCriticality(prototype.getCriticality())
      .withExpectations(prototype.getExpectations());
  }

  public static ServiceDependencyBuilder restServiceDependency(final String url) {
    return new ServiceDependencyBuilder().withUrl(url)
      .withType(ServiceDependency.TYPE_SERVICE)
      .withSubtype(ServiceDependency.SUBTYPE_REST)
      .withMethods(singletonList("GET"))
      .withMediaTypes(singletonList("application/json"));
  }

  public static ServiceDependencyBuilder serviceDependency(final String url) {
    return new ServiceDependencyBuilder().withUrl(url)
      .withType(ServiceDependency.TYPE_SERVICE)
      .withSubtype(ServiceDependency.SUBTYPE_OTHER);
  }

  public ServiceDependencyBuilder withName(final String name) {
    this.name = name;
    return this;
  }

  public ServiceDependencyBuilder withDescription(final String description) {
    this.description = description;
    return this;
  }

  private ServiceDependencyBuilder withUrl(final String url) {
    this.url = url;
    return this;
  }

  public ServiceDependencyBuilder withType(final String type) {
    this.type = type;
    return this;
  }

  public ServiceDependencyBuilder withSubtype(final String subtype) {
    this.subtype = subtype;
    return this;
  }

  public ServiceDependencyBuilder withMethods(final List<String> methods) {
    this.methods = methods;
    return this;
  }

  public ServiceDependencyBuilder withMethods(final String... methods) {
    this.methods = asList(methods);
    return this;
  }

  public ServiceDependencyBuilder withMediaTypes(final List<String> mediaTypes) {
    this.mediaTypes = mediaTypes;
    return this;
  }

  public ServiceDependencyBuilder withMediaTypes(final String... mediaTypes) {
    this.mediaTypes = asList(mediaTypes);
    return this;
  }

  public ServiceDependencyBuilder withAuthentication(final String authentication) {
    this.authentication = authentication;
    return this;
  }

  public ServiceDependencyBuilder withCriticality(final Criticality criticality) {
    this.criticality = criticality;
    return this;
  }

  public ServiceDependencyBuilder withExpectations(final Expectations expectations) {
    this.expectations = expectations;
    return this;
  }

  public ServiceDependency build() {
    return new ServiceDependency(name, description, url, type, subtype, methods, mediaTypes, authentication, criticality,
      expectations);
  }
}
